(ns atticus.factory
  "Factory methods to generate defrecord instances for use in testing.  Inspired by
notahat's machinist gem.

You can refer to (lexically) previous fields when defining the factory.

     (defrecords make-rec my.ns.SomeRecord
       :n 1 :o (inc (:n %)))

     (make-rec)
       => {:n 1 :o 2}

Overriding a single value will cause it to be picked up in computed expressions
     (make-rec :n 2)
       => {:n 2 :o 3}

Computed fields can also be set explicitly.
   (make-user :o 1)
     => {:n 1 :o 1}")

(defn clj-fields
  "Obtain a list of a defrecord's fields"
  [^Class record-type]
  (letfn [(is-clj-field?
           [^java.lang.reflect.Field field]
           (and
            (= (bit-or
                java.lang.reflect.Modifier/PUBLIC
                java.lang.reflect.Modifier/FINAL)
               (.getModifiers field))
            (not (.startsWith (.getName field) "__"))))]
    (map
     #(keyword (.getName ^java.lang.reflect.Field %))
     (filter is-clj-field? (.getDeclaredFields record-type)))))


(defmacro invoke-default-constructor
  [record-type]
  (let [klass (resolve record-type)
        fields (clj-fields klass)
        num-fields (count fields)]
    `(let [constructor# (first
                         (filter
                          (fn [c#] (= ~num-fields (count (.getParameterTypes c#))))
                          (.getDeclaredConstructors ~record-type)))]
       ((memfn ~(symbol "newInstance") args#) ;~@(map (comp symbol name) fields)
        constructor#
        (object-array ~(vec (repeat num-fields nil)))))))

(defn set-fields
  "Set the specified field values."
  [m obj]
  (if (seq m)
    (set-fields
     (rest m)
     `(let [obj# ~obj]
        (if (~(ffirst m) obj#) ; don't override invocation args
          obj#
          (apply assoc obj#
                 ((fn [a#]
                    (let [~(symbol "%") a#]
                      ~(first m))
                    )
                  obj#)))))
    obj))


(defmacro defrecords
  "Create a record factory function.

   e.g. (defrecords make-rec my.ns.SomeRecord
          :n 1 :o (inc (:n %)))"
  [name record-type & kw-vals]
  (let [kw-map (apply hash-map kw-vals)]
    `(defn ~name [& ~(symbol "defrecords-options")]
       ~(set-fields
         kw-map
         `(merge (invoke-default-constructor ~record-type)
                 (apply hash-map ~'defrecords-options))))))
