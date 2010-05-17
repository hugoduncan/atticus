(ns atticus.mock-test
  (:use atticus.mock :reload-all)
  (:use clojure.test)
  (:require
   [clojure.contrib.condition :as condition]))

(deftest verify-expectations-test
  (is (thrown?
       clojure.contrib.condition.Condition
       (verify-expectations [(fn [] (condition/raise :error 1))])))
  (is (nil?
       (verify-expectations [(fn [] true)]))))

(defn equality-condition
  [a b msg]
  (println "MM " a b)
  (if-not (= a b)
    (condition/raise :message msg)
    (println "HEY " a b)))

(deftest add-expectation-test
  (with-expectations
    (add-expectation (fn [] "x"))
    (is (= "x" ((first *expectations*))))))

(deftest once-f-test
  (binding [*equality-checker* equality-condition]
    (with-expectations
      (let [f (once v1 [] (once 1))]
	(is (thrown?
	     clojure.contrib.condition.Condition
	     ((first *expectations*))))
	(is (= 1 (f)))
	(is (nil? ((first *expectations*))))))
    (with-expectations
      (let [f (once v1 [x] (once (inc x)))]
	(is (thrown?
	     clojure.contrib.condition.Condition
	     ((first *expectations*))))
	(is (= 1 (f 0)))
	(is (nil? ((first *expectations*))))))))

(deftest times-f-test
  (binding [*equality-checker* equality-condition]
    (with-expectations
      (let [f (times v1 [x] (times 2 (inc x)))]
	(is (thrown?
	     clojure.contrib.condition.Condition
	     ((first *expectations*))))
	(is (= 1 (f 0)))
	(is (= 2 (f 1)))
	(is (nil? ((first *expectations*))))))
    (with-expectations
      (let [f (times v1 [x] (times 2 (inc x)))]
	(is (thrown?
	     clojure.contrib.condition.Condition
	     ((first *expectations*))))
	(is (= 1 (f 0)))
	(is (= 2 (f 1)))
	(is (nil? ((first *expectations*))))))))

(deftest construct-mock-test
  (let [mock (construct-mock  `(v1 [] true))]
    (is (= `(fn [] true)))))

(deftest add-mock-test
  (is (= ['a.b.c `(fn [] true)] (add-mock [] `(a.b.c [] true)))))

(deftest construct-bindings-test
  (is (= [] (construct-bindings '[])))
  (is (= ['a.b.c `(fn [] true)] (construct-bindings `[(a.b.c [] true)])))
  (is (vector? (construct-bindings [`(a.b.c [] true)]))))


(def x)

(deftest expects-test
  (expects []
    (is (= [] *expectations*)))
  (expects [(x [] true)]
    (is (= [] *expectations*))
    (is (x)))
  (expects [(x [] false)]
    (is (= [] *expectations*))
    (is (not (x)))))

(deftest once-test
  (is (nil?
       (expects [(x [] (once true))]
         (is (= 1 (count *expectations*)))
         (is (x))))))

(deftest times-test
  (is (nil?
       (expects [(x [] (times 2 true))]
         (is (= 1 (count *expectations*)))
         (is (x))
	 (is (x))))))
