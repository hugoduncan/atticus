(ns atticus.stub-test
  (:use atticus.stub :reload-all)
  (:use clojure.test))

(deftest eval-constant-test
  (let [value-fn (fn [] (eval-constant (rand)))
        value (value-fn)]
    (is (every? #(= value %) (repeatedly 10 value-fn)))
    (is (identical? value (value-fn)))))
