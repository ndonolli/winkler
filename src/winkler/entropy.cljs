(ns winkler.entropy
  (:require [clojure.core.async :refer [go go-loop >! <! chan] :as a]
            [winkler.utils :refer [floor log2 abs] :as u]))

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
             (-> (+ i x) (js/Math.log) (js/Math.sqrt) (js/Math.sin))))))

; timer_race_loop: ->
;     @_last_count = null
;     while @running
;       if @count_unused_bits() < @auto_stop_bits
;         count = @millisecond_count()
;         if @_last_count? and (delta = count - @_last_count)
;           entropy = Math.max 0, (Math.floor(@log_2 Math.abs delta) - 1)
;           entropy = Math.min @max_bits_per_delta, entropy
;           v       = [delta, entropy]
;           @entropies.push v
;         @_last_count = count
;       await @delay defer()

; (defn gen-entropies [entropies]
;   (go-loop [last nil]
;     ))

(defn calc-entropy
  "Calculates bits of entropy given a delta value.  Takes a numerical delta value and a max-bit amount."
  ([delta] (calc-entropy delta 4))
  ([delta max-bits]
   (-> (log2 (abs delta))
       (dec) (floor)
       (max 0)
       (min max-bits))))

(defn generate [bits]
  (let [entropies (chan 128)]))

(comment
  (calc-entropy 929823 100000))

