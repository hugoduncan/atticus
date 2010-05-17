(ns atticus.utils-test
  (:use atticus.utils :reload-all)
  (:use clojure.test)
  (:require
   [clojure.contrib.condition :as condition]))

(deftest tmpfile-test
  (let [t (tmpfile)]
    (is (.canRead t))
    (.delete t))
  (let [t (tmpfile "pfx" "sfx")]
    (is (.contains (.getName t) "pfx"))
    (is (.contains (.getName t) "sfx"))
    java.io.File
    (is (.canRead t))
    (.delete t)))


(def *fn*)

(deftest with-temporary-file-test
  (binding [*fn* nil]
    (with-temporary-file [t (tmpfile)]
      (set! *fn* t))
    (is (not (.canRead *fn*)))))
