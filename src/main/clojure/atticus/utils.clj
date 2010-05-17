(ns atticus.utils
  (:require
   [clojure.contrib.logging :as logging])
  (:use clojure.test))

(defmacro with-private-vars [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context.  From users mailing
list, Alan Dipert and MeikelBrandmeyer."
  `(let ~(reduce #(conj %1 %2 `@(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))

(defn tmpfile
  "Create a temporary file."
  ([] (tmpfile "atticus" "test"))
  ([prefix suffix]
     (java.io.File/createTempFile prefix suffix)))

(defmacro with-temporary-file
  "Create bindings for temporary files. delete will be called on exit of the
   block."
  [bindings & body] {:pre [(vector? bindings)
         (even? (count bindings))]}
  (cond
   (= (count bindings) 0) `(do ~@body)
   (symbol? (bindings 0)) `(let ~(subvec bindings 0 2)
                             (try
                              (with-temporary-file ~(subvec bindings 2) ~@body)
                              (finally
                               (. ~(bindings 0) delete))))
   :else (throw (IllegalArgumentException.
                 "with-temporary only allows Symbols in bindings"))))
