(ns winkler.core
  (:require [winkler.entropy :refer
             [DEFAULT collect-entropy ms-count calc-entropy under-harvest-limit?]]
            [winkler.utils :refer [create-promise]]
            [clojure.core.async :refer [go-loop chan close! >! <!]]))

(defn generate
  "Returns a lazy sequence of integers with increasing bits of entropy. Can optionally provide additional options map:

   - `:entropy` - takes from the sequence until the combined entropy is at least the given amount (default 100).
   - `:max-bits` - max entropy value allowed per generation (default 4).
   - `:work-min` - minimum time period (in ms) per each loop of operation crunching (default 1).

   ```clojure
   ;; Generate random integers with at least 100 bits of combined entropy
   (generate {:entropy 100}) ;; => (1134 -419 16631 -2872 ...)

   ;; Without any arguments, `generate` will produce infinitely. So take precautions:
   (take 3 (generate)) ;; => (5081 -1092 -4678)
   ;; Although lazy, each take does require running timed computations in order to calculate entropy values.
   ```"
  ([] (generate DEFAULT))
  ([opts]
   (let [{:keys [max-bits entropy] :as opts*} (merge DEFAULT opts)]
     (->> (collect-entropy opts*)
          (reductions (fn [[_ harvested] [delta _ bits]]
                        [delta (+ harvested bits)])
                      [0 0])
          (take-while (fn [[_ harvested]]
                        (under-harvest-limit? entropy max-bits harvested)))
          (map first)
          (rest)))))

(defn generate-promise
  "Returns a Promise resolving a lazy sequence of integers with increasing bits of entropy. Best used if a high work-min or entropy amount is required, as this may noticably block the main thread.

   An options map argument is required, but may be empty:

   - `:entropy` - takes from the sequence until the combined entropy is at least the given amount (default 100).
   - `:max-bits` - max entropy value allowed per generation (default 4).
   - `:work-min` - minimum time period (in ms) per each loop of operation crunching (default 1).

   The last positional argument is a callback function to be called at the resolution of the sequence generation.

   ```clojure
   ;; Generate random integers with at least 1000 (!) bits of combined entropy
   (generate-promise {:entropy 1000} println) ;; => (1134 -419 16631 -2872 ...)
   ```"
  [opts callback] (create-promise (generate opts) callback))

(defn generate-async
  "Returns a core.async channel of integers with increasing bits of entropy. Channel will close upon reaching the end of the generation sequence. Best used if a high work-min or entropy amount is required, as this may noticably block the main thread. Can optionally provide additional options map:

   - `:entropy` - takes from the sequence until the combined entropy is at least the given amount (default 100).
   - `:max-bits` - max entropy value allowed per generation (default 4).
   - `:work-min` - minimum time period (in ms) per each loop of operation crunching (default 1).
   - `:buffer` - channel buffer size (default 10).

   ```clojure
   ;; Asynchronously print random integers with at least 1000 (!) bits of combiend entropy
   (let [ch (generate-async {:entropy 1000})]
    (go-loop []
      (if-let [entropy (<! ch)]
        (do (println entropy)
            (recur)))))
   ```"
  ([] (generate-async DEFAULT))
  ([opts]
   (let [{:keys [work-min entropy buffer max-bits]} (merge DEFAULT opts)
         entropies (chan buffer)]
     (go-loop [last-val nil
               harvested 0]
       (let [value (ms-count work-min)
             delta (- value last-val)
             bits (calc-entropy delta)]
         (if (under-harvest-limit? entropy max-bits harvested)
           (do (when last-val (>! entropies delta))
               (recur value (+ bits harvested)))
           (close! entropies))))
     entropies)))