(ns atticus.stub
  "Stub objects for testing.")

(defmacro stub
  "Create a stub object. A wrapper for reify."
  [& specs]
  `(reify ~@specs))

(defmacro eval-constant
  "Macro to create a form that is evaluated once, and then always returns the
   same value.  Useful if you want to use generated values (eg. with faker)
   in your stubs."
  [form]
  (let [x (gensym "eval-const-")]
    `(do
       (defonce ~x ~form)
       ~x)))
