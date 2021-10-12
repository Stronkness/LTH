(ns edaa40.lab1)

(use 'clojure.set)

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
;;	SETS
;;

(declare cartesian)

  (defn cartesian
;;   "computes Cartesian product of A and B"
    [A B]

	  (set(for[a A b B] [a b]))
	)
 (test? "cartesian test 1" (cartesian #{1 2} #{3 4 5}) #{[1 3] [1 4] [1 5] [2 3] [2 4] [2 5]})


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

(declare rng)

(defn rng
;;   "computes the range of a relation"

   [R]

   (set (for [r R] (second r)))
 )

 (test? "rng" (rng #{[1 :a] [2 :b] [1 :c] [3 :a]}) #{:a :b :c})


(declare image-of)

 (defn image-of
;;   "computes the image of the element x under R"

;;;; prepare a seq of all keys//ref
   [R x]

   (set (remove nil? (set (for [r R] (when (= (first r) x) (second r))))))

    ;; uses "set", "for" with :when, "first", "second"
 )

(test? "image-of 1" (image-of #{[1 :a] [2 :b] [1 :c] [3 :a]} 1) #{:a :c})

(test? "image-of 2" (image-of #{[1 :a] [2 :b] [1 :c] [3 :a]} 3) #{:a})

(test? "image-of 3" (image-of #{[1 :a] [2 :b] [1 :c] [3 :a]} 4) #{})


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

(declare inverse)

(defn inverse
;;   "computes the inverse (aka converse) of a relation"
   [R]

   (set (for [r R] (reverse r)))
)

 (test? "inverse" (inverse #{[1 :a] [2 :b] [1 :c] [3 :a]}) #{[:a 1] [:b 2] [:c 1] [:a 3]})



(def converse inverse)   ;; because that's how mathematicians roll


(defn complement-relation 

  [R A B]
  
  (set (for [a A b B :when (not (contains? R [a b]))] [a b]))
)

(declare reflexive?)

(defn reflexive?
;;   "tests whether R is reflexive over A"

   [R A]

   (every? #(contains? R [% %]) A)
   ;; uses "every?", "contains?"
 )

(test? "reflexive 1" (reflexive? #{[1 1] [1 2] [1 3] [2 2] [2 3] [3 3]} #{1 2 3}) true)

(test? "reflexive 2" (reflexive? #{[1 1] [1 2] [1 3] [2 2] [2 3] [3 3]} #{1 2 3 4}) false)

(test? "reflexive 3" (reflexive? #{[1 1] [1 2] [1 3] [2 3] [3 3]} #{1 2 3}) false)



(defn irreflexive? 
  "tests whether R is irreflexive over its domain"

  [R]

  (every? #(not (contains? R [% %])) (dom R))
)

(declare symmetric?)

(defn symmetric?
;;   "tests whether R is symmetric"

   [R]

   (every? #(contains? R (inv1 %)) R)
   ;; uses "every?", "contains?", "inv1"
 )

 (test? "symmetric 1" (symmetric? #{[1 1] [1 2] [1 3] [2 2] [2 3] [3 3]}) false)

 (test? "symmetric 2" (symmetric? #{[1 1] [1 3] [2 2] [3 1]}) true)


(declare transitive?)

 (defn transitive?
;;   "tests whether R is transitive"

   [R]

   ;; hint: you might want to exploit the fact that R is transitive iff for every
   ;;       pair (a, b) in R the image of b under R is a subset of the image
   ;;       of a under R

   (every? #(subset? (image-of R (second %)) (image-of R (first %))) R)
   ;; uses "every?", "subset?", "image-of", "second", "first"
 )


 (test? "transitive 1" (transitive? #{[1 1] [1 2] [1 3] [2 2] [2 3] [3 3]}) true)
 
 (test? "transitive 2" (transitive? #{[1 1] [1 2] [1 3] [2 2] [3 1] [3 3]}) false)


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

(declare surjective?)

 (defn surjective? 
;;   "determines whether f is surjective on codomain B"
   
   [f B]
 
   (= (rng f) B)
 )
 
 (test? "surjective 1" (surjective? #{[1 1] [2 2] [3 3]} #{1 2 3}) true) 
 
 (test? "surjective 2" (surjective? #{[1 1] [2 2] [3 3]} #{1 2 3 4}) false)
 
 (test? "surjective 3" (surjective? #{[1 1] [2 2] [3 1]} #{1 2 3}) false) 


(defn bijective? 
  "determines whether f is a bijection from its domain to codomain B"

  [f B]

  (and (injective? f) (surjective? f B))
)

