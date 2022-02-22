(ns edaa40.test.lab1)

(use 'clojure.test)
(use 'clojure.set)
(use 'edaa40.core)
(use 'edaa40.lab1)
  
(def ps (powerset #{1 2 3 4}))

(def A (set (range 0 10)))

(def B (set (range 1 20)))

(def r1 (set (for [a A b A :when (< a b)] [a b])))

(def r2 (set (for [a A b A :when (<= a b)] [a b])))

(def r3 (set (for [a B b B :when (divides? a b)] [a b])))

(def r4 (set (for [a B b B :when (coprime? a b)] [a b])))

(def r5 (intersection r1 (inverse r1)))

(def r6 (intersection r2 (inverse r2)))

(def r7 (compose r4 r3))

(def r8 (compose r3 r4))


(defn relation-properties [R A]
  [
    :reflexive (reflexive? R A) 
    :symmetric (symmetric? R) 
    :transitive (transitive? R) 
    :asymmetric (asymmetric? R) 
    :antisymmetric (antisymmetric? R)
    :strict-order (strict-order? R)
    :non-strict-order (non-strict-order? R A)
    :total (total? R A)
    :equivalence (equivalence? R A)
  ]
)

(def r1props (relation-properties r1 A))

(def r2props (relation-properties r2 A))

(def r3props (relation-properties r3 B))

(def r4props (relation-properties r4 B))

(def r5props (relation-properties r5 A))

(def r6props (relation-properties r6 A))

(def r7props (relation-properties r7 A))

(def r8props (relation-properties r8 A))


(def X (set (range -9 10)))

(def Y (set (range 0 10)))

(def Z (set (range 0 100)))

(def f1 (set (for [x X] [x (* x x)])))

(def f2 (set (for [x Y] [x (* x x)])))

(def f3 (set (for [x Z] [x (mod x 11)])))

(def f4 (compose f3 f2))

(def f5 (set (for [x X] [x (- x)])))

(defn function-properties [f A]
  [
    :injective (injective? f)
    :surjective (surjective? f A)
    :bijective (bijective? f A)
  ]
)

(def f1props (function-properties f1 Z))

(def f2props (function-properties f2 Z))

(def f3props (function-properties f3 Z))

(def f4props (function-properties f4 Z))

(def f5props (function-properties f5 X))


;;
;;  tests
;;

(deftest sets
  (is (= ps
         #{#{} #{1} #{2} #{3} #{1 2} #{4} #{1 3} #{2 3} #{1 4} #{2 4} #{1 2 3} #{3 4} #{1 2 4} #{1 3 4} #{2 3 4} #{1 2 3 4}}))
)

(deftest relations
  (is (= r1props 
         [:reflexive false :symmetric false :transitive true :asymmetric true :antisymmetric true :strict-order true :non-strict-order false :total true :equivalence false]))
  (is (= r2props
         [:reflexive true :symmetric false :transitive true :asymmetric false :antisymmetric true :strict-order false :non-strict-order true :total true :equivalence false]))
  (is (= r3props
         [:reflexive true :symmetric false :transitive true :asymmetric false :antisymmetric true :strict-order false :non-strict-order true :total false :equivalence false]))
  (is (= r4props
         [:reflexive false :symmetric true :transitive false :asymmetric false :antisymmetric false :strict-order false :non-strict-order false :total false :equivalence false]))
  (is (= r5props
         [:reflexive false :symmetric true :transitive true :asymmetric true :antisymmetric true :strict-order true :non-strict-order false :total false :equivalence false]))
  (is (= r6props
         [:reflexive true :symmetric true :transitive true :asymmetric false :antisymmetric true :strict-order false :non-strict-order true :total false :equivalence true]))
  (is (= r7props
         [:reflexive false :symmetric true :transitive false :asymmetric false :antisymmetric false :strict-order false :non-strict-order false :total false :equivalence false]))
  (is (= r8props
         [:reflexive false :symmetric true :transitive true :asymmetric false :antisymmetric false :strict-order false :non-strict-order false :total false :equivalence false]))
)

(deftest functions
  (is (= f1props [:injective false :surjective false :bijective false]))
  (is (= f2props [:injective true :surjective false :bijective false]))
  (is (= f3props [:injective false :surjective false :bijective false]))
  (is (= f4props [:injective false :surjective false :bijective false]))
  (is (= f5props [:injective true :surjective true :bijective true]))
)  



