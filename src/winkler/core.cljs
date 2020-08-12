(ns winkler.core
  (:require [winkler.entropy :refer [DEFAULT collect-entropy]]
            [winkler.utils :refer [defer]]
            [clojure.core.async :as a]))

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
   ```"
  ([] (generate DEFAULT))
  ([opts]
   (let [{:keys [max-bits entropy callback] :as opts*} (merge DEFAULT opts)
         entropies (->> (collect-entropy opts*)
                        (reductions (fn [[_ harvested] [delta _ entropy]]
                                      [delta (+ harvested entropy)])
                                    [0 0])
                        (take-while (fn [[_ harvested]]
                                      (>= (+ entropy (* 2 max-bits)) harvested)))
                        (map first)
                        (rest))]
     (if callback
       (defer entropies callback)
       entropies))))

(comment
  (do
    (take 3 (generate {:work-min 1 :entropy 100 :callback #(println %)}))
    (println "doesn't block")))

;;  (let [ch (a/to-chan! (take 3 (generate {:work-min 1000})))]
;;    (a/go-loop []
;;      (if-let [d (a/<! ch)]
;;        (do
;;          (println d)
;;          (recur))))
;;    (println "doing")
;;    (println "other stuff"))