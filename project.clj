(defproject atticus "0.1.0-SNAPSHOT"
  :description "Atticus - test helper library"
  :url "http://github.com/hugpduncan/atticus"
  :source-path "src/main/clojure"
  :test-path "src/test/clojure"
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
		 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]]
  :dev-dependencies [[leiningen/lein-swank "1.2.0-SNAPSHOT"]
                     [autodoc "0.7.0"]]
  :repositories [["build.clojure.org" "http://build.clojure.org/releases/"]
		 ["clojars.org" "http://clojars.org/repo/"]]
  :autodoc {:name "Atticus"
	    :description "Atticus is a test helper library."
	    :copyright "Copyright Hugo Duncan 2010. All rights reserved."
	    :web-src-dir "http://github.com/hugoduncan/atticus/blob/"
	    :web-home "http://hugoduncan.github.com/atticus/" })
