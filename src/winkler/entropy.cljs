(ns winkler.entropy
  (:require [winkler.utils :refer [floor log log2 sqrt abs sin rrest] :as u]))

(def default-opts {:lazy-loop-delay 30
                    :loop-delay 5
                    :work-min 1})

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
  ([delta] (calc-entropy delta 4))
  ([delta max-bits]
   (-> (log2 (abs delta))
       (dec) (floor)
       (max 0)
       (min max-bits))))

(defn collect-entropy
  "Returns a lazy-seq of generated entropy data. Each is a 3-tuple consisting of [delta value entropy]"
  []
  (rrest
   (iterate
    (fn [[_ last-val _]]
      (let [value (ms-count 1)
            delta (- value last-val)
            entropy (calc-entropy delta)]
        [delta value entropy]))
    [nil nil nil])))

(defn generate
  "Returns a sequence of integers"
  [bit-limit]
  (->> (collect-entropy)
       (reductions (fn [[_ harvested] [delta _ entropy]]
                     [delta (+ harvested entropy)])
                   [0 0])
       (take-while (fn [[_ harvested]] (>= bit-limit harvested)))
       (map first)
       (rest)))

(comment
  (take 4 (collect-entropy))
  (let [xs (generate 100)]
    (->> xs
         (partition 2 1)
         (map #(calc-entropy (- (last %) (first %))))
         (reduce +))))
