(ns winkler.entropy
  (:require [winkler.utils :refer [floor log log2 sqrt abs sin rrest] :as u]))

(defonce DEFAULT {:max-bits 4
                  :bit-limit 100})

(defn ms-count
  "Returns the number of floating point operations executed in the time limit provided in the argument (in ms)."
  [work-min]
  (loop [start (js/Date.now)
         i 0
         x 0]
    (if (>= (js/Date.now) (+ start work-min 1))
      i
      (recur start
             (inc i)
             (-> (+ i x) log sqrt sin)))))

(defn calc-entropy
  "Calculates bits of entropy given a delta value.  Takes a numerical delta value and a max-bit amount."
  ([delta] (calc-entropy delta (:max-bits DEFAULT)))
  ([delta max-bits]
   (-> (log2 (abs delta))
       (dec) (floor)
       (max 0)
       (min max-bits))))

(defn collect-entropy
  "Returns a lazy-seq of generated entropy data. Each element is a 3-tuple consisting of [delta value entropy]"
  ([] (collect-entropy (:max-bits DEFAULT)))
  ([max-bits]
   (rrest
    (iterate
     (fn [[_ last-val _]]
       (let [value (ms-count 1)
             delta (- value last-val)
             entropy (calc-entropy delta max-bits)]
         [delta value entropy]))
     [nil nil nil]))))
