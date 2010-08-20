(ns atticus.factory-test
  (:use atticus.factory :reload-all)
  (:use clojure.test))

(defrecord TestRecord [a b])

(deftest clj-fields-test
  (is (= [:a :b] (clj-fields TestRecord))))

(deftest defrecords-test
  (defrecords make-rec TestRecord
    :a 1 :b (inc (:a %)))
  (is (= (TestRecord. 1 2) (make-rec)))
  (is (= (TestRecord. 2 3) (make-rec :a 2)))
  (is (= (TestRecord. 1 1) (make-rec :b 1))))
