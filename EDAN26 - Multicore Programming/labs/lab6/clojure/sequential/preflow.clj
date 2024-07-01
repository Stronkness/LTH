(require '[clojure.string :as str])		; for splitting an input line into words

(def debug false)

(defn prepend [list value] (cons value list))	; put value at the front of list

(defrecord node [i e h adj])			; index excess-preflow height adjacency-list

(defn node-adj [u] (:adj u))			; get the adjacency-list of a node
(defn node-height [u] (:h u))			; get the height of a node
(defn node-excess [u] (:e u))			; get the excess-preflow of a node

(defn has-excess [u nodes]
  (> (node-excess @(nodes u)) 0))

(defrecord edge [u v f c])			; one-node another-node flow capacity
(defn edge-flow [e] (:f e))			; get the current flow on an edge
(defn edge-capacity [e] (:c e))			; get the capacity of an edge

; read the m edges with the normal format "u v c"
(defn read-graph [i m nodes edges]
  (if (< i m)
    (do	(let [line 	(read-line)
              words	(str/split line #" ")
              u		(Integer/parseInt (first words))
              v 	(Integer/parseInt (first (rest words)))
              c 	(Integer/parseInt (first (rest (rest words))))] 

          (ref-set (edges i) (update @(edges i) :u + u))
          (ref-set (edges i) (update @(edges i) :v + v))
          (ref-set (edges i) (update @(edges i) :c + c))

          (ref-set (nodes u) (update @(nodes u) :adj prepend i))
          (ref-set (nodes v) (update @(nodes v) :adj prepend i)))

        ; read remaining edges
        (recur (+ i 1) m nodes edges))))


(defn other [edge u]
  (if (= (:u edge) u) (:v edge) (:u edge)))

(defn u-is-edge-u [edge u]
  (= (:u edge) u))

(defn increase-flow [edges i d]
  (ref-set (edges i) (update @(edges i) :f + d)))

(defn decrease-flow [edges i d]
  (ref-set (edges i) (update @(edges i) :f - d)))

(defn move-excess [nodes u v d]
  (ref-set (nodes u) (update @(nodes u) :e - d))
  (ref-set (nodes v) (update @(nodes v) :e + d)) 
  (when debug
    (println "----- move-excess -----")
    (println "d =" d)
    (println "u =" u)
    (println "v =" v)
    (println "ue =" (node-excess @(nodes u)))
    (println "ve =" (node-excess @(nodes v))))
  )

(defn insert [excess-nodes v]
  (ref-set excess-nodes (cons v @excess-nodes)))

(defn check-insert [excess-nodes v s t]
  (when (and (not= v s) (not= v t))
    (insert excess-nodes v)))

(defn push [edge-index u nodes edges excess-nodes change s t]
  (let [v 	(other @(edges edge-index) u)
        uh	(node-height @(nodes u))
        vh	(node-height @(nodes v))
        e 	(node-excess @(nodes u))
        i		edge-index
        f 	(edge-flow @(edges i))
        c 	(edge-capacity @(edges i))
        d   (if (u-is-edge-u @(edges i) u)
              (min e (- c f))
              (min e (+ c f)))]

    (when debug
      (println "--------- push -------------------")
      (println "i = " i)
      (println "u = " u)
      (println "uh = " uh)
      (println "e = " e)
      (println "f = " f)
      (println "c = " c)
      (println "v = " v)
      (println "vh = " vh)
      (println "d =" d))
    
    (if (u-is-edge-u @(edges i) u)
      (increase-flow edges i d)
      (decrease-flow edges i d))
    
    (move-excess nodes u v d)

    (when (has-excess u nodes)
      (check-insert excess-nodes u s t))
    
    (when (== (node-excess @(nodes v)) d)
      (check-insert excess-nodes v s t)) 
    ))

(defn relabel [u nodes excess-nodes s t]
  (when debug
    (println "----- relabel -----")
    (println "u =" u)
    (println "uh =" (node-height @(nodes u)))) 
  (ref-set (nodes u) (update @(nodes u) :h + 1))
  (check-insert excess-nodes u s t))

(defn dispatch [u nodes edges excess-nodes s t]
  (loop [remaining (node-adj @(nodes u))]
    (if (empty? remaining)
      (relabel u nodes excess-nodes s t)
      (let [i (first remaining)
            e @(edges i)
            v (other e u)
            b (if (u-is-edge-u e u) 1 -1)
            uh (node-height @(nodes u))
            vh (node-height @(nodes v))
            f (edge-flow e)
            c (edge-capacity e)]   
        
        (when debug
          (println "----- dispatch -----")
          (println "i =" i)
          (println "u =" u)
          (println "v =" v)
          (println "uh =" uh)
          (println "vh =" vh)
          (println "b =" b)
          (println "f =" f)
          (println "c =" c))
        (if (and (> uh vh) (< (* b f) c))
          (push i u nodes edges excess-nodes 1 s t)
          (recur (rest remaining)))
        ))))

; go through adjacency-list of source and push
(defn initial-push [adj s t nodes edges excess-nodes]
  (let [change (ref 0)] ; unused for initial pushes since we know they will be performed
    (when (seq adj)
      (let [ei (first adj)]
      ; give source this capacity as excess so the push will be accepted
      (ref-set (nodes s) (update @(nodes s) :e + (edge-capacity @(edges ei))))
      (push ei s nodes edges excess-nodes change s t)
      (initial-push (rest adj) s t nodes edges excess-nodes)))))

(defn initial-pushes [nodes edges s t excess-nodes]
  (initial-push (node-adj @(nodes s)) s t nodes edges excess-nodes))

(defn remove-any [excess-nodes]
  (dosync
   (let [u (ref -1)]
     (when (seq @excess-nodes)
       (ref-set u (first @excess-nodes))
       (ref-set excess-nodes (rest @excess-nodes)))
     @u)))

; read first line with n m c p from stdin

(def line (read-line))

; split it into words
(def words (str/split line #" "))

(def n (Integer/parseInt (first words)))
(def m (Integer/parseInt (first (rest words))))

(def s 0)
(def t (- n 1))
(def excess-nodes (ref ()))

(def nodes (vec (for [i (range n)] (ref (->node i 0 (if (= i 0) n 0) '())))))

(def edges (vec (for [i (range m)] (ref (->edge 0 0 0 0)))))

(dosync (read-graph 0 m nodes edges))

(defn preflow []

  (dosync (initial-pushes nodes edges s t excess-nodes))

  (println "----- NODES / EDGES -----")
  (println @(nodes 0))
  (println @(nodes 1))
  (println @(nodes 2))
  
  (println @(edges 0)) 
  (println @(edges 1))
  (println "----- NODES / EDGES -----")

  (while (seq @excess-nodes)
    (let [u (remove-any excess-nodes)]
     	(dosync (dispatch u nodes edges excess-nodes s t))
     	)
   	)

  (println "f =" (node-excess @(nodes t))))

(preflow)
