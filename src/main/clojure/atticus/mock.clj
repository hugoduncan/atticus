(ns atticus.mock
  "Simple Mocking in Clojure. Allows you to implement the mock function as a
lambda.

(deftest with-1-test
  (expects
   [(f [arg] (is (= arg 1) \"Check argument\") arg)]
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

(def ^:dynamic *expectations*)

(defn equality-checker
  [actual expected msg]
  (is (= actual expected) msg))

(def ^:dynamic *equality-checker* equality-checker)

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
  [[v args & body]]
  (if (and (list? (first body))
           (#{#'once #'times} (resolve (first (first body)))))
    `(~(first (first body)) ~v ~args ~@body)
    `(fn ~args ~@body)))

(defn extract-functions [mock-proto]
  (subvec (vec mock-proto) 2))

(defn protocol-mock? [spec]
  (not (vector? (second spec))))

(defn protocol-function-map [function-map spec]
  (assoc function-map (-> spec
               name
               keyword)
         spec))

(declare construct-bindings)

(defn add-mock
  "Add a mock to the bindings."
  [mocks mock]
  (if (protocol-mock? mock)
    (concat mocks (construct-bindings (extract-functions mock)))
    (concat mocks [(first mock) (construct-mock mock)])))

(defn construct-bindings
  "Construct a binding vector from the mock specification."
  [mocks]
  (vec (reduce add-mock [] mocks)))

(defmacro with-expectations
  [& body]
  `(binding [*expectations* []]
     ~@body))

(defn create-protocol-impl [extend-type protocol fns]
  `(extend ~extend-type
     ~protocol
     ~(reduce protocol-function-map {}
              (map #(first (fns %)) (range 0 (count fns))))))

(defn- create-protocol-pairs [v mock]
  (concat v [(nth mock 0)
             (create-protocol-impl nil (nth mock 1) (extract-functions mock))]))
(defn construct-protocol-bindings [mocks]
  (->> mocks
       (filter protocol-mock?)
       (reduce create-protocol-pairs [])
       vec))

(defmacro expects
  "Binds a list of mocks, checling any expectations on exit of the block."
  [mocks & body]
  `(with-expectations
     (with-redefs ~(construct-bindings mocks)
       (let ~(construct-protocol-bindings mocks)
         ~@body))
     (verify-expectations *expectations*)))
