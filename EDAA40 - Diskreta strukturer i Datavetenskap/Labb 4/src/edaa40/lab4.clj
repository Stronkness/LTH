(ns edaa40.lab4)

(use 'clojure.set)
(use 'edaa40.core)


(declare create-square-sum-relation)

(declare H)
(declare H')

(declare next-positions)
(declare create-knights-move-relation)

;;
;; test functions
;;

(defn- square-sum-sequence?
    "tests whether S is a square sum sequence, or whether it is a
     square sum sequence of the first n positive natural numbers"

    (
        [S]
    
        (case (count S)
            0   true
            1   true
            (and (square? (+ (first S) (second S))) (square-sum-sequence? (rest S)))
        )
    )
    (
        [S n]
        
        (and (= n (count S)) 
             (= (set S) (set (range 1 (inc n)))) 
             (square-sum-sequence? S)
        )
    )
)

(declare board)
    
(defn- knights-tour?
    "checks whether the path P is in fact a knight's tour on board B"

    (
        [P B]

        (if (<= (count P) 1)
            (= (set P) B)            
            (and
                (B (first P))
                ((next-positions (first P) B) (second P))
                (knights-tour? (rest P) (disj B (first P)))
            )
        )
    )
    (
        [P n m]
        
        (knights-tour? P (board n m))
    )
)




;;
;; Part A: square sum problem
;;


 (defn create-square-sum-relation
     "given a set A of integers, produces a relation that includes tuple [a b],
     iff a and b are in A and their sum is a square number"

     [A]

     (set (for [a A b A :when (square? (+ a b ))] [a b]))

     ;; this one should be easy
     ;; use square? to test whether a number is a square

 )

 (test? "create-square-sum-relation 1" (create-square-sum-relation #{3 6}) #{[3 6] [6 3]})
 (test? "create-square-sum-relation 2" (create-square-sum-relation #{3 7 19}) #{})
 (test? "create-square-sum-relation 3" (create-square-sum-relation (set (range 1 11))) #{[5 4] [2 2] [8 8] [4 5] [7 9] [1 3] [3 6] [6 10] [2 7] [1 8] [8 1] [7 2] [10 6] [6 3] [3 1] [9 7]})
 

(defn square-sum-sequence
    "computes a list of the n first positive natural numbers such that any two 
    consecutive numbers in that list add up to a square; return s nil if no
    such sequence exists"

    [n]
    
    (let
        [A (set (range 1 (inc n)))]
        
        (H A (create-square-sum-relation A))
    )
)


;;
;; Part B: Hamiltonian path
;;

 (defn- H'

    "This is the helper function for computing the Hamiltonian path.
      E is the relation, i.e. the graph, we are looking for a path in.
      a is the current node.
      S is the set of nodes we haven't visited yet.
      P is the path we have traveled so far.

      H' should return a Hamiltonian path through E
      that begins with P, then goes through a, and then visits every vertex
      in the set S.
      If no such path exists, it should return nil."

     [E a S P]

     {
     :pre [
             (not (contains? S a))
             (not (contains? (set P) a))
             (empty? (intersection S (set P)))
         ]
     :post [
             (or (empty? %) (= (set %) (union S (set P) #{a})))
             (or (empty? %) (= (count %) (+ (count S) (count P) 1)))
           ]
     }


     (if (empty? S) (concat P [a]) (some #(H' E % (disj S %) (concat P [a])) (intersection (image-of E a) S)))

     ;; CAUTION: make sure you write the body of the function HERE
     ;;          after the :pre/:post condition map.

     ;; in my implementation, I used concat, disj, intersection, some, empty?
     ;; and our old buddy image-of
     ;; (concat P [a]) will append a to the end of P

 )

(defn H
    "compute a Hamiltonian path in the graph (V, E); returns a list of the elements in V in the
    order of that path, or nil if no such path exists"

    [V E]

    (some #(H' E % (disj V %) '()) V)
)


 (test? "square-sum-sequence 1" (count (square-sum-sequence 14)) 0)
 (test? "square-sum-sequence 2" (square-sum-sequence? (square-sum-sequence 15) 15))
 (test? "square-sum-sequence 3" (square-sum-sequence? (square-sum-sequence 16) 16))
 (test? "square-sum-sequence 4" (square-sum-sequence? (square-sum-sequence 17) 17))
 (test? "square-sum-sequence 5" (count (square-sum-sequence 18)) 0)
 (test? "square-sum-sequence 6" (count (square-sum-sequence 19)) 0)
 (test? "square-sum-sequence 7" (count (square-sum-sequence 22)) 0)
 (test? "square-sum-sequence 8" (square-sum-sequence? (square-sum-sequence 23) 23))
 (test? "square-sum-sequence 9" (count (square-sum-sequence 24)) 0)
 (test? "square-sum-sequence 10" (square-sum-sequence? (square-sum-sequence 25) 25))
 (test? "square-sum-sequence 11" (square-sum-sequence? (square-sum-sequence 26) 26))
 (test? "square-sum-sequence 12" (square-sum-sequence? (square-sum-sequence 27) 27))


;;
;; You can now try to get a few square sum sequences in the REPL, e.g.
;;      (square-sum-sequence 15)
;;


;;
;; Part C: knight's tour problem
;;

(defn board
    "computes the set of all positions on an n by m board; each position is a tuple of integers, 
    from 0 to n-1 and 0 to m-1, respectively"

    [n m]
    
    (set (for [a (range 0 n) b (range 0 m)] [a b]))
)


(def Moves #{ [1 2] [2 1] [2 -1] [1 -2] [-1 -2] [-2 -1] [-2 1] [-1 2] })


(defn add-move
    "adds a move, i.e. relative coordinates, to a position, resulting in the coordinates
    of the target position after the move (which may be outside the board)"

    [pos move]

    (vec (map + pos move))
)

(defn next-positions
     "given a position pos and a board B, this computes the set of all positions on the board
     after any of the moves in Moves"

     [pos B]

     (intersection B (set (map #(add-move pos %) Moves)))

     ;; I used map, set, intersection to write this
)

 (test? "next-positions 1" (next-positions [0 0] (board 3 3)) #{[2 1] [1 2]})
 (test? "next-positions 2" (next-positions [1 1] (board 3 3)) #{})
 (test? "next-positions 3" (next-positions [2 3] (board 8 8)) #{[4 4] [1 1] [3 5] [0 2] [0 4] [1 5] [3 1] [4 2]})


 (defn create-knights-move-relation

     [B]

     (set (for[b B next(next-positions b B)] [b next]))
     ;; if you got this far, this should be no big deal
 )

 (test? "create-knights-move-relation 1" (create-knights-move-relation (board 2 3)) #{[[1 2] [0 0]] [[0 0] [1 2]] [[1 0] [0 2]] [[0 2] [1 0]]})
 (test? "create-knights-move-relation 2" (create-knights-move-relation (board 3 3)) #{[[0 0] [2 1]] [[0 1] [2 2]] [[2 2] [1 0]] [[1 2] [0 0]] [[0 0] [1 2]] [[2 1] [0 0]] [[2 2] [0 1]] [[1 0] [2 2]] [[2 0] [0 1]] [[2 1] [0 2]] [[2 0] [1 2]] [[1 0] [0 2]] [[1 2] [2 0]] [[0 1] [2 0]] [[0 2] [1 0]] [[0 2] [2 1]]})
 (test? "create-knights-move-relation 3" (create-knights-move-relation (board 2 2)) #{})
 (test? "create-knights-move-relation 4" #{} #{})


(defn knights-tour

    [n m]
    
    (let
        [B (board n m)]
        
        (H B (create-knights-move-relation B))
    )
)


 (test? "knights-tour 1" (knights-tour 3 3) nil)
 (test? "knights-tour 2" (knights-tour? (knights-tour 3 4) 3 4))
 (test? "knights-tour 3" (knights-tour? (knights-tour 4 5) 4 5))



;; Now try to find a few knight's tours:
;; (the results below are examples --- depending on the details of your implementation, the path you
;; find may be different)

;; (knights-tour 3 3)
;; nil
;;
;; (knights-tour 3 4)
;; ([1 3] [0 1] [2 0] [1 2] [0 0] [2 1] [0 2] [2 3] [1 1] [0 3] [2 2] [1 0])
;;
;; (knights-tour 5 6)
;; ([4 0] [3 2] [1 1] [3 0] [4 2] [3 4] [1 5] [0 3] [2 2] [4 1] [2 0] [0 1] [1 3] [0 5] [2 4] [4 5] [3 3] [1 4] [0 2] [1 0] [3 1] [4 3] [3 5] [2 3] [4 4] [2 5] [0 4] [1 2] [0 0] [2 1])
;;
;; (knights-tour 6 6)
;; ([2 5] [4 4] [5 2] [4 0] [2 1] [3 3] [1 4] [3 5] [5 4] [4 2] [5 0] [3 1] [4 3] [5 5] [3 4] [1 5] [0 3] [1 1] [3 0] [5 1] [3 2] [2 0] [4 1] [5 3] [4 5] [2 4] [0 5] [1 3] [0 1] [2 2] [1 0] [0 2] [2 3] [0 4] [1 2] [0 0])]
;;
;; (knights-tour 6 7)
;; ([5 6] [3 5] [4 3] [5 1] [3 0] [4 2] [5 0] [3 1] [2 3] [1 1] [0 3] [1 5] [3 6] [5 5] [3 4] [1 3] [0 5] [2 6] [1 4] [0 6] [2 5] [4 6] [5 4] [3 3] [4 1] [2 0] [0 1] [2 2] [1 0] [0 2] [2 1] [4 0] [5 2] [4 4] [3 2] [5 3] [4 5] [2 4] [1 6] [0 4] [1 2] [0 0])

;; The running time for this algorithm explodes very quickly as the boards get larger. 
;; My old little laptop takes about 20min or so to find a path on a 6 by 7 board (time can vary wildly depending on the order in which nodes are being
;; explored, so comparisons of single runs don't really mean much), I have run out of patience for 8 by 8. 
;; For timing the algorithm, you can use time-eval (the source is in the core package) as follows:
;;      (time-eval (knights-tour 6 6))
;; It returns a two-element vector, the first component is the running time (in nanoseconds), and the second the value returned.
;;


