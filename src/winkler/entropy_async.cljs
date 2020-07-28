(ns winkler.entropy-async
  (:require [clojure.core.async :refer [go go-loop >! <! chan] :as a]
            [winkler.entropy :refer [ms-count calc-entropy]]))

(defn collect-entropy!
  ([] (collect-entropy! (chan 128)))
  ([entropies]
   (go-loop [last-count nil]
     (let [count (ms-count 1)
           delta (- count last-count)]
       (if last-count
         (>! entropies [delta (calc-entropy delta)]))
       (recur count)))))


;; (defn generate [bit-limit]
;;   (let [entropies (chan 128)]
;;     (collect-entropy! entropies)
;;     (go-loop [entropy (<! entropies)
;;               bits 0]
;;       ())))