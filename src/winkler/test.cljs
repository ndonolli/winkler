(ns winkler.test
  (:require [cljs.test :refer [is deftest testing]]
            [winkler.entropy :refer [generate calc-entropy]]))

(deftest generate-test
  (testing "integer sequence contains total entropy of at least provided bit-limit"
    (letfn [(total-entropy [coll]
             (->> coll
                  (partition 2 1)
                  (map #(calc-entropy (- (last %) (first %))))
                  (reduce +)))]
      (doseq [n-bits (map (partial * 100) (range 6))]
        (is (>= (total-entropy (generate n-bits)) n-bits))))))
