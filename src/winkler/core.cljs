(ns winkler.core
  (:require [winkler.entropy :refer [DEFAULT collect-entropy]]))

(defn generate
  "Returns a sequence of integers with at least n bits of entropy, as provided by the bit-limit. Takes an optional argument for a max-bit per delta in the sequnce. Defaults to 4"
  ([bit-limit] (generate bit-limit (:max-bits DEFAULT)))
  ([bit-limit max-bits]
   (->> (collect-entropy max-bits)
        (reductions (fn [[_ harvested] [delta _ entropy]]
                      [delta (+ harvested entropy)])
                    [0 0])
        (take-while (fn [[_ harvested]]
                      (>= (+ bit-limit (* 2 max-bits)) harvested)))
        (map first)
        (rest))))