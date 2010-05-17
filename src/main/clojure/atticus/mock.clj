(ns atticus.mock
  "Simple Mocking in Clojure. Allows you to implement the mock function as a
lambda.

(deftest with-1-test
  (expects
   [(f [arg] (do (is (= arg 1) \"Check argument\") arg))]
   (is (= 1 (f 1)) \"Call mocked function\")))

(deftest with-1-once-test
  (expects
   [(f [arg] (once (inc arg)))]
   (is (= 1 (f 1)) \"Call mocked function\")))

(deftest with-1-times-test
  (expects
   [(f [arg] (times 2 arg))]
   (is (= 1 (f 1)) \"Call mocked function\")
   (is (= 1 (f 1)) \"Call mocked function\")))
"
  (:use clojure.test))

(def *expectations*)

(defn equality-checker
  [a b msg]
  `(is (= ~a ~b ~msg)))

(def *equality-checker* equality-checker)

(defn verify-expectations
  [checks]
  (doseq [check checks]
    (check)))

(defn add-expectation
  "Add an expectation check function to the list of expectations"
  [f]
  (set! *expectations* (conj *expectations* f)))

(defmacro once
  "Add an expectation that the function is called once."
  [v args body]
  `(let [counter# (atom 0)]
     (add-expectation
      (fn []
	(*equality-checker*
	 @counter# 1
	 (format "Expected one call to %s. %d seen." '~v @counter#))))
     (fn [& args#]
       (swap! counter# inc)
       (apply (fn ~args ~@(rest body)) args#))))

(defmacro times
  "Add an expectation that the function is called specified number of times."
  [v args body]
  `(let [counter# (atom 0)
	 n# ~(second body)]
     (add-expectation
      (fn []
	(*equality-checker*
	 @counter# n#
	 (format "Expected %d calls to %s. %d seen." n# '~v @counter#))))
     (fn [& args#]
       (swap! counter# inc)
       (apply (fn ~args ~@(nnext body)) args#))))

(defn construct-mock
  "Construct the mock. Checks for a mock wrapper around the body."
  [[v args body]]
  (if (and (list? body) (#{#'once #'times} (resolve (first body))))
    `(~(first body) ~v ~args ~body)
    `(fn ~args ~@(if (seq? body) body (list body)))))

(defn add-mock
  "Add a mock to the bindings."
  [mocks mock]
  (concat mocks [(first mock) (construct-mock mock)]))

(defn construct-bindings
  "Construct a binding vector from the mock specification."
  [mocks]
  (vec (reduce add-mock [] mocks)))

(defmacro with-expectations
  [& body]
  `(binding [*expectations* []]
     ~@body))

(defmacro expects
  "Binds a list of mocks, checling any expectations on exit of the block."
  [mocks & body]
  `(with-expectations
     (binding ~(construct-bindings mocks)
       ~@body)
     (verify-expectations *expectations*)))
