(ns edaa40.lab2)

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
;; Relations
;;

;; these are some functions from the previous lab that might
;; be useful in implementing the stuff below (wink, wink...)

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

  (set 
    (for [p R :when (= x (first p))] 
      (second p)
    )
  )
)

;;
;; PageRank
;;

 (def TinyWeb #{[0 1][0 2][1 2][2 1][2 3]})
;; define the relation in the figure of the instructions
;;
 (test? "TinyWeb 1" (dom TinyWeb) #{0 1 2})
 (test? "TinyWeb 2" (rng TinyWeb) #{1 2 3})
 (test? "TinyWeb 3" (image-of TinyWeb 0) #{1 2})


(def alpha 90/100)

(declare all-pages)

 (defn all-pages
;;   "computes set of all pages on the web"

   [web]

   (union (dom web)(rng web))
;;   hint: check out the Clojure function "union"
 )

 (test? "all-pages" (all-pages TinyWeb) #{0 1 2 3})


(declare no-links?)

 (defn no-links?
;;   "determines whether a page in a web has no outgoing links"
;;   
   [web page]

       (=(count(image-of web page))0)
 )
;; 
 (test? "no-links? 1" (no-links? TinyWeb 0) false)
 (test? "no-links? 2" (no-links? TinyWeb 3) true)

(declare random-page)

 (defn random-page
;;   "picks a random page occurring in a web"

   [web]

   (rand-nth(seq(all-pages web)))
;;   hint: Check out "rand-nth". Also, if you need to convert a set into a sequence, 
;;         you want to take a look at "seq".
 )

 (test? "random-page" (number? (random-page TinyWeb)))

(declare random-link)

 (defn random-link
;;   "picks a random outgoing link from a page in a web,
;;   returns the page the link points to"

   [web page]

   (rand-nth(seq(image-of web page)))
   ;;   hint: If you've done the previous one, this one should be easy.
 )
;; 


(declare surf)
 
 (defn surf
;;   "simulates one step by a surfer,
;;   with boredom probability 1-alpha,
;;   returns the page the surfer goes to"

   [web current]
   ;;(if (or (< (rand) 0.1) (no-links? web current)) (random-page web) (random-link web current))
   (if (< (rand 1) 0.1) (random-page web) (if(no-links? web current) (random-page web) (random-link web current)))
;;   hint: check out "rand"
   )

(defn random-surfer
  "simulates a random surfer for a number of steps, returns page visit stats"
  
  [web steps]

  (loop [current (random-page web)
         visits (into {} (map #(vector % 0) (all-pages web)))
         i steps]
    (if (zero? i)
      visits
      (recur (surf web current) (assoc visits current (inc (visits current))) (dec i))
    )
  )
)

(defn page-rank
  "produces random surfing visit stats scaled down by the number of steps"

  [web steps]
  
  (let
    [visits (random-surfer web steps)]

    (into {} (map #(vector % (double (/ (visits %) steps))) (keys visits)))
  )
)

;; you should run this eventually in the repl:
;; (page-rank TinyWeb 100000)
;;
;; it should result in something close to this:
;; {0 0.082061, 1 0.288512, 2 0.378093, 3 0.251334}
