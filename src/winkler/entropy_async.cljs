(ns winkler.entropy-async
  (:require [clojure.core.async :refer [go-loop >! close! chan] :as a]
            [winkler.entropy :refer [ms-count calc-entropy DEFAULT]]))

;; WORK IN PROGRESS - async versions of some of the winkler.entropy functions.
;; Although winkler.core.generate is lazy, the computation is expensive as the sequence grows.
;; This namespace will coordinate through channels should larger entropy sets be needed.
;; For most cases, winkler.core will suffice and will not require the core.async dependency.

(defn collect-entropy-async
  "Returns an async channel of generated entropy data. Each element is a 3-tuple consisting of [delta value entropy]"
  ([] (collect-entropy-async DEFAULT))
  ([opts]
   (let [{:keys [work-min buffer max-bits]} (merge DEFAULT opts)
         entropies (chan 10)]
     (go-loop [last-val nil
               harvested 0]
       (let [value (ms-count work-min)
             delta (- value last-val)
             entropy (calc-entropy delta)]
         (if (and last-val (not (>= (+ entropy (* 2 max-bits)) harvested)))
           (do (>! entropies delta)
               (recur value (+ entropy harvested)))
           (close! entropies))))
     entropies)))

(comment
  (let [c (collect-entropy-async)]
    (a/go-loop []
      (if-let [d (a/<! c)]
        (do (js/console.log d)
            (recur))))))