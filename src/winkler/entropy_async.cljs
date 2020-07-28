(ns winkler.entropy-async
  (:require [clojure.core.async :refer [go-loop >! chan] :as a]
            [winkler.entropy :refer [ms-count calc-entropy]]))

;; WORK IN PROGRESS - async versions of some of the winkler.entropy functions.
;; Although winkler.core.generate is lazy, the computation is expensive as the sequence grows.
;; This namespace will coordinate through channels should larger entropy sets be needed.
;; For most cases, winkler.core will suffice and will not require the core.async dependency.

(defonce DEFAULT {:buffer 128})

(defn collect-entropy
  "Returns an async channel of generated entropy data. Each element is a 3-tuple consisting of [delta value entropy]"
  ([] (collect-entropy (chan (:buffer DEFAULT))))
  ([entropies]
   (go-loop [last-val nil]
     (let [value (ms-count 1)
           delta (- value last-val)]
       (if last-val
         (>! entropies [delta value (calc-entropy delta)]))
       (recur count)))
   entropies))