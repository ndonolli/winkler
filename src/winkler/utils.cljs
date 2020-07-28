(ns winkler.utils)

(defn abs
  "Math.abs wrapper"
  [x] (js/Math.abs x))

(defn floor
  "Math.floor wrapper"
  [x] (js/Math.floor x))

(defn log
  "Math.log wrapper"
  [x] (js/Math.log x))

(defn sqrt
  "Math.sqrt wrapper"
  [x] (js/Math.sqrt x))

(defn sin
  "Math.sin wrapper"
  [x] (js/Math.sin x))

(defn log2
  "Division of Math.log(x) / Math.LN2"
  [x] (/ (log x) (.-LN2 js/Math)))

(defn rrest
  "Rest of rest"
  [s] (rest (rest s)))