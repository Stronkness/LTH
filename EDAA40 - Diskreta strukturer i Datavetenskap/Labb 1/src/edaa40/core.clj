(ns edaa40.core)

(use 'clojure.set)
 

(defn factorial [n]
  (if (< n 2)
    1
    (* n (factorial (- n 1)))
  )
)

(defn fib [n]
  (if (< n 2)
    1
    (+ (fib (- n 1)) (fib (- n 2)))
  )
)

(defn gcd 
  "computes the greatest common divisor of two integers"
  [a b]
  (if (= b 0)
    a
    (gcd b (mod a b))
  )
)

(defn divides? 
  "returns true of a divides b evenly, i.e. without remainder"
  [a b] 
  (= (rem b a) 0)
)

(defn prime? 
  "true, iff n is a prime"
  [n]
  (and
    (> n 1)
    (empty? (filter (fn [k] (divides? k n)) (range 2 n)))
  )
)

(defn primes-to 
  "computes the primes up to n"
  [n]
  (filter prime? (range 2 (+ n 1)))
)

(defn factors-of 
  "computes a list of all factors of an integer n"
  [n]
  (filter (fn [k] (= (rem n k) 0)) (range 1 (+ n 1)))
)

(defn prime-factors-of 
  "computes a list of all prime factors of an integer n"
  [n]
  (filter prime? (factors-of n))
)

(defn coprime? [a b]
  (empty? (intersection (set (prime-factors-of a)) (set (prime-factors-of b))))
)

