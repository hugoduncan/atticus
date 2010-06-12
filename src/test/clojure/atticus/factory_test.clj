(ns atticus.factory-test
  (:use atticus.factory :reload-all)
  (:use clojure.test))

(defrecord TestRecord [a b])

(deftest clj-fields-test
  (is (= [:a :b] (clj-fields TestRecord))))

(deftest defrecords-test
  (defrecords make-rec TestRecord
    :a 1 :b (inc (:a %)))
  (is (= {:a 1 :b 2} (make-rec)))
  (is (= {:a 2 :b 3} (make-rec :a 2)))
  (is (= {:a 1 :b 1} (make-rec :b 1))))
