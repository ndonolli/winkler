(ns winkler.test
  (:require [cljs.test :refer [is deftest testing async]]
            [clojure.core.async :refer [go-loop <!]]
            [winkler.core :refer [generate generate-promise generate-async]]
            [winkler.entropy :refer [calc-entropy]]))

(defn total-entropy [coll]
  (->> coll
       (partition 2 1)
       (map #(calc-entropy (- (last %) (first %))))
       (reduce +)))

(deftest generate-test
  (testing "integer sequence contains total entropy of at least provided bit-limit"
    (doseq [n-bits (map (partial * 100) (range 6))]
      (is (>= (total-entropy (generate {:entropy n-bits})) n-bits)))))


(deftest generate-promise-test
  (testing "integer sequence contains total entropy of a high bit-limit"
    (async done
           (generate-promise
            {:entropy 2000}
            (fn [result]
              (is (>= (total-entropy result) 2000))
              (done))))))

(deftest generate-async-test
  (testing "integer sequence contains total entropy of a high bit-limit"
    (async done
           (let [ch (generate-async {:entropy 2000})]
             (go-loop [result (seq [])]
               (if-let [entropy (<! ch)]
                 (recur (cons entropy result))
                 (do (is (>= (total-entropy result) 2000))
                     (done))))))))


