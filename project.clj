(defproject atticus "0.1.0-SNAPSHOT"
  :description "Atticus - test helper library"
  :url "http://github.com/hugpduncan/atticus"
  :source-path "src/main/clojure"
  :test-path "src/test/clojure"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
                     [autodoc "0.7.1"]]
  :autodoc {:name "Atticus"
            :description "Atticus is a test helper library."
            :copyright "Copyright Hugo Duncan 2010. All rights reserved."
            :web-src-dir "http://github.com/hugoduncan/atticus/blob/"
            :web-home "http://hugoduncan.github.com/atticus/" })
