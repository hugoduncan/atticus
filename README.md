# Atticus

atticus is a embryonic collection of tools to help testing.

atticus.mock is a slightly different approach to mocking, where the mocked
function can be implemented as an inline function to do whatever checking or
return value calculation that is required.

	;; pull in namespaces
        (use 'clojure.test)
        (require 'atticus.mock)

	;; define test which mocks f
	(deftest mock-test
          (atticus.mock/expects
            [(f [arg]
               (is (= arg 1) "Check argument")
               arg)]
	    (is (= 1 (f 1)) "Call mocked function"))


At the moment there are two macros that can be used to wrap the body of the
mocked function and will add verification for the number of times the mocked
function is called. The `once` macro ensures the function is called once, and
only once. The `times` macro, which takes an integer argument, ensures the
function is called exactlyt he specified number of times.

	;; define test, that should be called just once
	(deftest mock-test
          (atticus.mock/expects
            [(f [arg]
               (atticus.mock/once
                 (is (= arg 1) "Check argument")
                 arg))]
	    (is (= 1 (f 1)) "Call mocked function"))

atticus.utils contains some test helpers for accessing private vars, and for
handling temporary files.

[API documentation](http://hugoduncan.github.com/atticus) is available.

## Installation

atticus is distributed as a jar, and is available in the [clojars repository](http://clojars.org/atticus).

Installation is with [Leiningen](http://github.com/technomancy/leiningen),
maven, or your favourite Maven repository aware build tool.

## License

Licensed under [EPL](http://www.eclipse.org/legal/epl-v10.html)
