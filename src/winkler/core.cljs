(ns winkler.core
  (:require [winkler.entropy :refer [DEFAULT collect-entropy]]))

(defn generate
  "Returns a lazy sequence of integers with increasing bits of entropy. Can optionally provide additional options map:

   - `:entropy` - takes from the sequence until the combined entropy is at least the given amount (default nil).
   - `:max-bits` - max entropy value allowed per generation (default 4).
   - `:work-min` - minimum time period (in ms) per each loop of operation crunching (default 1).

   ```clojure
   ;; Generate random integers with at least 100 bits of combined entropy
   (generate {:entropy 100}) ;; => (1134 -419 16631 -2872 ...)

   ;; Without any arguments, `generate` will produce infinitely. So take precautions:
   (take 3 (generate)) ;; => (5081 -1092 -4678)
   ;; Although lazy, each take does require running timed computations in order to calculate entropy values.
   ```

  ([] (generate DEFAULT))"
  ([opts]
   (let [{:keys [max-bits entropy] :as opts*} (merge DEFAULT opts)]
     (cond->> (collect-entropy opts*)
       true
       (reductions (fn [[_ harvested] [delta _ entropy]]
                     [delta (+ harvested entropy)])
                   [0 0])
       entropy
       (take-while (fn [[_ harvested]]
                     (>= (+ entropy (* 2 max-bits)) harvested)))
       true (map first)
       true (rest)))))
