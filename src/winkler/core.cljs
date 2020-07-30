(ns winkler.core
  (:require [winkler.entropy :refer [DEFAULT collect-entropy]]))

(defn generate
  "Returns a lazy sequence of integers with increasing bits of entropy. Can optionally provide additional options in the form:

   - `:entropy` - takes from the sequence until the combined entropy is at least the given amount.
   - `:max-bits` - the max entropy value allowed per generation (default 4).

   ```clojure
   ;; Generate random integers with at least 100 bits of combined entropy
   (generate :entropy 100) ;; => (1134 -419 16631 -2872 ...)

   ;; Without any arguments, `generate` will produce infinitely. So take precautions:
   (take 3 (generate)) ;; => (5081 -1092 -4678)
   ;; Although lazy, each take does require running timed computations in order to calculate entropy values.
   ```
   "
  ([& {:keys [entropy max-bits]
       :or {entropy nil
            max-bits (:max-bits DEFAULT)}}]
   (cond->> (collect-entropy max-bits)
     true
     (reductions (fn [[_ harvested] [delta _ entropy]]
                   [delta (+ harvested entropy)])
                 [0 0])
     entropy
     (take-while (fn [[_ harvested]]
                   (>= (+ entropy (* 2 max-bits)) harvested)))
     true (map first)
     true (rest))))

(comment
  (take 9 (generate)))