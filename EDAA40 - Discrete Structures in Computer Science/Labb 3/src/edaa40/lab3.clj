(ns edaa40.lab3)

(use 'edaa40.core)

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


;; indices of winning lines
;; this can be used by threeinarow to determine
;; whether a player has won

(def winning-lines [
  [0 1 2]
  [3 4 5]
  [6 7 8]
  [0 3 6]
  [1 4 7]
  [2 5 8]
  [2 4 6]
  [0 4 8] ]
)

;; these definitions have variables point to the symbols representing them;
;; the reason is so that we can avoid "quoting" the X, O, and _ symbols all the time

(def X 'X)

(def O 'O)

(def _ '_)

;; this is the empty board

(def B0 [_ _ _
         _ _ _
         _ _ _] )

;; if X gets to move next in this one, it wins:

(def B1 [_ O _
         _ X _
         _ _ _])

;; this shows why O needs to answer in the corner, if X
;; takes the center in its first move: if X gets the
;; next move in this position, it cannot do better than a draw

(def B2 [O _ _
         _ X _
         _ _ _])
         

(declare threeinarow)

 (defn- threeinarow
;;   "determines whether the specified line (a vector of three indices) is 
;;    fully occupied by player p in the board b"

   [p b ln]

   (every? true?(for[v ln] (if(=(b v) p) true false)))

;;  hint: this is a very simple one; it becomes even simpler when you remember that 
;;        the = operator can take any number of arguments, and that lines and the board
;;        are vectors, and that if v is a vector and n is a number, then (v n) is the n-th
;;        element of the vector (starting from 0, as usual)
 )


 (test? "threeinarow 1" (threeinarow X [_ _ _ _ _ _ X X X] (winning-lines 2)))
 (test? "threeinarow 2" (not (threeinarow X [_ _ _ _ _ _ X X X]  (winning-lines 1))))
 (test? "threeinarow 3" (not (threeinarow O [_ _ _ _ _ _ X X X]  (winning-lines 2))))
;; 

(declare win?)

 (defn win?
;;   "determines whether player p has fully occupied at least one of
;;    the lines in the variable winning-lines"

   [p b]

   (some true?(for[l winning-lines] (threeinarow p b l)))
;;   hint: of course, this one uses threeinarow and winning-lines. Also, "some". 
 )

 (test? "win? 1" (win? X [X _ _ X _ _ X _ _]))
 (test? "win? 2" (not (win? O [X _ _ X _ _ X _ _])))

(defn opponent 
  "computes the opponent of the specified player"

  [p]

  (case p
    X O
    O X
    _)
)

(declare moves)

 (defn moves
;;   "computes all possible moves player p can make on board b;
;;    it returns a list of all possible new boards after p made a move,
;;    or an empty list, if p cannot make any move"

   [p b]

   (map #(assoc b % p) (filter #(if(= (b %) _ )true false) (range 9)))
;;  hint: uses map, assoc, filter, and range.
;;        "assoc" is useful to "replace", in a vector such as the one representing the board,
;;        one element with another. (assoc b n p) returns a vector that is like b, except that 
;;        p is the next value at index n.
;;        "filter" is used to filter out those boards whose n-th field is open, i.e. has the value _.

;;        All iteration happens in map and filter --- if you start writing loops, try to find a solution
;;        using the functions listed above.
 )

 (test? "moves 1" (count (moves X B0)) 9)
 (test? "moves 2" (count (moves X B1)) 7)
 (test? "moves 3" (count (moves X B2)) 7)



;; A "game tree" is a map that has the following structure:
;; {:player <player> :board <some board> :win <winner> :children <list of game trees>}
;;
;; <player> : is either X or O, and signifies the player whose move is next
;; <some board> : is a board, i.e. a tictactoe configuration
;; <winner> : is either X, O, or _, depending on whether in this configuration, 
;;            X wins, O wins, or we get a draw (assuming optimal play).

(defn gametree 
  "computes the game tree that starts from board position b,
   with player p moving next"
   
  [p b]

  (cond
    (win? X b)	{:player p :board b :win X :children '()}	;; X has won
    (win? O b)	{:player p :board b :win O :children '()}	;; O has won
    :else
      (let 
        [
          c (map #(gametree (opponent p) %) (moves p b))       ;; all possible moves
          w (cond
              (empty? c)			_		;; no moves: this is a draw
              (some #(= (% :win) p) c)		p		;; at least one winning move: s wins
              (some #(= (% :win) _) c)		_		;; no winner, but at least one move to a draw: draw
              :else 				(opponent p)	;; all moves lead to opponent win, :-(
            )
        ]
        
        { :player p :board b :children c :win w }
      )
  )
)

;; If you have implemented "moves" and it passes the tests, try uncommenting these.
;; 
 (def B0-GT (gametree X B0))

 (def B1-GT (gametree X B1))

 (def B2-GT (gametree X B2))

(declare gametree-count)

 (defn gametree-count
   "counts the number of nodes in a game tree"

   [t]

   (inc(reduce + (map gametree-count (t :children))))


;;  hint: uses empty?, inc, reduce, +, map
;;        With "map" you recursively compute a list of node counts of all
;;        children. Check out "reduce" to see how this gets you to
;;        thier sum. Of course, "empty?" is needed to make sure you 
;;        don't do this if a node does not have children.
 )

 (test? "gametree-count B0" (gametree-count B0-GT) 549946)
 (test? "gametree-count B1" (gametree-count B1-GT) 7064)
 (test? "gametree-count B2" (gametree-count B2-GT) 6812)


(defn reduce-gametree 
  "reduces the game tree by throwing away all nodes where the winner is not the same as the
   winner of the overall game"

  [t]
  
  (if (t :children)
    (assoc t :children (map reduce-gametree (filter #(= (% :win) (t :win)) (t :children))))
    t
  )
)

;; If all the tests pass up to here, try uncommenting this:

 (def B0-RGT (reduce-gametree B0-GT))

 (def B1-RGT (reduce-gametree B1-GT))

 (def B2-RGT (reduce-gametree B2-GT))


 (test? "reduce-gametree count B0" (gametree-count B0-RGT) 12134)
 (test? "reduce-gametree count B1" (gametree-count B1-RGT) 1765)
 (test? "reduce-gametree count B2" (gametree-count B2-RGT) 206)


(declare gametree-height)

 (defn gametree-height
;;   "computes the height of a game tree; a tree without children
;;    has height 1, otherwise it has the maximal height of all its
;;    children, plus 1"

   [t]

   (if (empty?(t :children)) 1 (inc(reduce max(map gametree-height (t :children)))))

;;  hint: uses empty?, inc, reduce, max, map
;;        With "map" you recursively compute a list of the height of all
;;        children. Check out "reduce" and "max" to see how this gets you to
;;        the maximal height. Of course, "empty?" is needed to make sure you 
;;        don't do this if a node does not have children.
 )

 (test? "height B0-GT" (gametree-height B0-GT) 10)
 (test? "height B1-GT" (gametree-height B1-GT) 8)
 (test? "height B2-GT" (gametree-height B2-GT) 8)


(defn choose-maxheight-gametrees
  "gets a list of game trees and picks those with the largest height,
   returns a list of those with the largest height"
  
  [ts]
  
  (if (empty? ts)
    ts
    (let
      [d (reduce max (map gametree-height ts))]
      
      (filter #(= d (gametree-height %)) ts)
    )
  )
)

(defn choose-minheight-gametrees
  "get a list of game trees and picks those with the smallest height,
   returns a list of those with the smallest height"
  
  [ts]
  
  (if (empty? ts)
    ts
    (let
      [d (reduce min (map gametree-height ts))]
      
      (filter #(= d (gametree-height %)) ts)
    )
  )
)

(defn- optimal-gametree-reduced
  "computes the optimal game tree from a reduced game tree"

  [rt]
  
  (if (rt :children)
    (let 
      [c (map optimal-gametree-reduced (rt :children))]

      (if (= (rt :player) (rt :win))
        (assoc rt :children (choose-minheight-gametrees c))
        (assoc rt :children (choose-maxheight-gametrees c))
      )
    )
    rt
  )
)

(defn optimal-gametree 
  "computes an optimal game tree.
   We call a tree optimal if it only contains moves where the 
   winning player chooses moves where it wins as quickly as possible,
   while a losing or drawing player tries to pick moves that put the
   loss or draw as far out as possible"

  [t]
  
  (optimal-gametree-reduced (reduce-gametree t))
)

;; If all the tests pass up to here, try uncommenting this:

 (def B0-OGT (optimal-gametree B0-GT))

 (def B1-OGT (optimal-gametree B1-GT))

 (def B2-OGT (optimal-gametree B2-GT))


 (test? "optimal-gametree count B0" (gametree-count B0-OGT) 12134)
 (test? "optimal-gametree count B1" (gametree-count B1-OGT) 123)
 (test? "optimal-gametree count B2" (gametree-count B2-OGT) 206)


(declare rand-moves)

 (defn rand-moves
;;   "compute a sequence of moves from a game tree by randomly picking a child at each node,
;;    returns a list of boards"

    [t]

   (if(empty?(t :children)) '() (cons (t :board) (rand-moves(rand-nth(t :children)))))

;;  hint: uses cons, empty?, rand-nth
;;        Returns a list where the first element is the board of the root node, followed by the
;;        list of boards produced from a randomly chosen child, or '() if there are no children.
 )


;; a small function to pretty-print a board, and one to print a list of boards,
;; and another one to print info on the top node in a game tree,
;; in case you'd like a play around a little with configurations

(defn print-board
  "prints a board configuration in three lines"
  
  [b]
  
  (println (b 0)(b 1)(b 2))
  (println (b 3)(b 4)(b 5))
  (println (b 6)(b 7)(b 8))
)

(defn print-boards
  "prints a sequence of boards"
    
  [bs]
    
  (doseq [b bs] (print-board b) (println "----------"))
  nil
)

(defn print-gametree-top
  "prints some info on a game tree"
  
  [t]

  (print-board (t :board))
  (println "next move: " (t :player))
  (println "winner: " (t :win))
  (println "winning config: " (if (or (win? X (t :board)) (win? O (t :board))) "yes" "no"))
  (println "children: " (count (t :children)))
  (println "height: " (gametree-height t))
  (println "nodes: " (gametree-count t))
)
  

;; try this a few times:

   (print-boards (rand-moves B0-OGT))

   (print-boards (rand-moves B1-OGT))

   (print-boards (rand-moves B2-OGT))




