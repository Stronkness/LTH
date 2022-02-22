(ns edaa40.core)

(use 'clojure.set)

;;
;;  stuff with numbers
;;

(defn third [S]
  (nth S 2)
)

(defn abs [v]
  (if (> 0 v) (-' v) v)
)

(defn sqrt [r] (java.lang.Math/sqrt r))

(defn sqr [n] (* n n))

(defn- isqrt' [n r]
  (let [nxtr (/ (+' r (/ n r)) 2)]
    (if (< (abs (-' nxtr r)) 1)
      nxtr
      (recur n nxtr)
    )
  )
)

(defn isqrt 
  "returns the largest integer not greater than the square root of n"

    [n]
  
    (if (>= n 1)
        (let [r (bigint (isqrt' n n))]
            (if (<= Long/MIN_VALUE r Long/MAX_VALUE)
                (long r)
                r
            )
        )
        0
    )
)

(defn isqrt2 
    "returns a pair [a b] of two non-negative integers such that n = a^2 + b and n < (a+1)^2"
  
    [n]
  
    (let [r (isqrt n) e (-' n (*' r r))] [r e])
)

(defn square?
    "tests whether n is a square number"

    [n]
    
    (= n (sqr (isqrt n)))
)


(defn gcd 
  "computes the greatest common divisor of two integers"
  [a b]
  (if (zero? b)
    a
    (recur b (mod a b))
  )
)

(defn lcm
  "computes the least common multiple of two integers"
  [a b]
  (/ (* a b) (gcd a b))
)


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

;;
;;  testing
;;


(defn test?

  (
    [msg v]
    
    (if v
      (println msg ": passed")
      (println msg ": ERROR")
    )
  )

  (
    [msg v1 v2]

    (if (= v1 v2)
      (println msg ": passed")
      (println msg ": ERROR -- got'" v1 "', expected '" v2 "'")
    )
  )

)


;;
;;  timing
;;

(defn nanoTime
    []
    
    (. System (nanoTime))
)

(defmacro time-eval

    [expr]
    
    `(let
        [start# (nanoTime) ret# ~expr end# (nanoTime)]
        
        [(- end# start#) ret#]
    )
)

;;
;;	SETS
;;

(declare cartesian)

(defn cartesian
  "computes Cartesian product of A and B"
  
  [A B]

  (set (for [a A b B] [a b]))
)


(defn powerset 
  "computes the powerset of S"

  [S]
  
  (if (empty? S) 
    '#{#{}}
    (let [a (first S) p (powerset (next S))]
      (union 
        p
        (map #(union #{a} %) p)
      )
    )
  )
)

;;
;;	RELATIONS
;;

(defn dom 
  "computes the domain of a relation"

  [R]
  
  (set (for [r R] (first r)))
)

(defn rng
  "computes the range of a relation"
  
  [R]

  (set (for [r R] (second r)))
)


(defn image-of 
  "computes the image of the element x under R"
  
  [R x] 

  (set (for [r R :when (= x (first r))] (second r))) 
)



(defn image-of-set 
  "computes the image of the set X under R"

  [R X]

  (apply union (map #(image-of R %) X))
)


(defn compose 
  "computes the composition of S and R"
  
  [S R]
  
  (set (apply union
    (for [r R] (map #(vector (first r) %) (image-of S (second r))))
  ))
)


(defn- inv1
  "flips a tuple"

  [[a b]]
  
  [b a]
)


(defn inverse 
  "computes the inverse (aka converse) of a relation"

  [R]

  (set (for [r R] (inv1 r)))
)


(def converse inverse)   ;; because that's how mathematicians roll


(defn complement-relation 

  [R A B]
  
  (set (for [a A b B :when (not (contains? R [a b]))] [a b]))
)


(defn reflexive?
  "tests whether R is reflexive over A"

  [R A]

  (every? #(contains? R [% %]) A)
)



(defn irreflexive? 
  "tests whether R is irreflexive over its domain"

  [R]

  (every? #(not (contains? R [% %])) (dom R))
)


(defn symmetric?
  "tests whether R is symmetric"

  [R]

  (every? #(contains? R (inv1 %)) R)
)


(defn transitive?
  "tests whether R is transitive"

  [R]

  
  (every? #(subset? (image-of R (second %)) (image-of R (first %))) R)
)



(defn asymmetric?
  "tests whether R is asymmetric"

  [R]
 
  (every?
    #(not (contains? R (inv1 %)))
    R
  )
)

(defn antisymmetric? 
  "tests whether R is antisymmetric"

  [R]
  
  (every?
    #(if (contains? R (inv1 %)) (= (first %) (second %)) true)
    R
  )
)

(defn strict-order? 
  "tests whether R is a strict (partial) order"

  [R]
  
  (and (irreflexive? R) (transitive? R))
)

(defn non-strict-order?
  "tests whether R is a non-strict (partial) order on A"

  [R A]
  
  (and (reflexive? R A) (antisymmetric? R) (transitive? R))
)

(defn total?
  "tests whether R is a total on A"

  [R A]
  
  (every? (fn [[a b]] (or (= a b) (R [a b]) (R [b a]))) (cartesian A A))
)

(defn equivalence?
  "tests whether R is an equivalence relation on A"

  [R A]
    
  (and (reflexive? R A) (symmetric? R) (transitive? R))
)



;;
;;	FUNCTIONS
;;

(defn isfunction? 
  "determines whether a relation R is a function with domain A"
  
  [R A]

  (every? #(= (count (image-of R %)) 1) A)
)

(defn injective? 
  "determines whether f is injective"
  
  [f]

  (let [g (inverse f)]
    (every?
      #(= 1 (count (image-of g %)))
      (dom g)
    )
  )
)

(defn surjective? 
  "determines whether f is surjective on codomain B"
  
  [f B]

  ;; uses "rng"
  
  (= (rng f) B)
)


(defn bijective? 
  "determines whether f is a bijection from its domain to codomain B"

  [f B]

  (and (injective? f) (surjective? f B))
)



