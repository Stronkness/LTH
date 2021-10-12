(ns edaa40.lab5
    (:use [edaa40.core]
          [edaa40.huffutil] )
)


;;
;;  some test data
;; 

(def ConstantSequence (vec (for [i (range 10000)] 11)) )

(def ConstantSequenceInitialQueue [ {:kind :leaf :value 11 :frequency 10000} ] )

(def ConstantSequenceTree {:kind :leaf :value 11 :frequency 10000} )

(def ConstantSequenceBits '() )

(def ConstantSequenceCodes {11 '()} )

(def ConstantSequenceHuffmanCode {:tree ConstantSequenceTree :length (count ConstantSequence) :bits '()} )



(def SimpleSequence [:a :a :a :b :b :c :a :a :b :a :c :b :d :a :a])

(def SimpleSequenceInitialQueue
   [{:kind :leaf :value :d :frequency 1} 
    {:kind :leaf :value :c :frequency 2} 
    {:kind :leaf :value :b :frequency 4}
    {:kind :leaf :value :a :frequency 8}] )

(def SimpleSequenceTree
    {:kind :branch, 
        :left {:kind :branch, 
            :left {:kind :branch, 
                :left {:kind :leaf, :value :d, :frequency 1}, 
                :right {:kind :leaf, :value :c, :frequency 2}, 
                :frequency 3}, 
            :right {:kind :leaf, :value :b, :frequency 4}, 
            :frequency 7}, 
        :right {:kind :leaf, :value :a, :frequency 8}, 
        :frequency 15} )
        
(def SimpleSequenceBits '(1 1 1 0 1 0 1 0 0 1 1 1 0 1 1 0 0 1 0 1 0 0 0 1 1))

(def SimpleSequenceCodes {:d '(0 0 0) :c '(0 0 1) :b '(0 1) :a '(1)})

(def SimpleSequenceHuffmanCode {:tree SimpleSequenceTree :length (count SimpleSequence) :bits SimpleSequenceBits})



(def TextBytes (slurp-bytes "data/txt"))

(def TextBytesInitialQueue 
    (let [ F (frequencies TextBytes) ]
        (sort-by :frequency (map #(make-leaf % (F %)) (keys F)))
    )
)

(def TextBytesLeaf {:kind :leaf, :value 122, :frequency 311} )

(def TextBytesQueueWithoutLeaf (filter #(not= % TextBytesLeaf) TextBytesInitialQueue) )


;;
;;  building a Huffman tree
;;

(declare insert-into-queue)

 (defn insert-into-queue
     "Inserts a tree T into the queue Q such that the entries in the queue are arranged in ascending order of frequency.
      Q is a list of trees ordered by the value of their :frequency field."
     [T Q]

     {
         :pre [
             (is-sorted-by? :frequency Q)
         ]
         :post [
             (is-sorted-by? :frequency %)
             (= (count %) (inc (count Q)))
         ]
     }

      (vec(sort-by :frequency (conj Q T)))
 )

 (test? "insert-into-queue 1" (insert-into-queue (last SimpleSequenceInitialQueue) (drop-last SimpleSequenceInitialQueue)) SimpleSequenceInitialQueue)
 (test? "insert-into-queue 2" (insert-into-queue TextBytesLeaf TextBytesQueueWithoutLeaf) TextBytesInitialQueue)


(declare create-tree)

 (defn create-tree
     "Recursively create a tree from a queue of trees ordered by frequency.
      Q is a list of trees ordered by the value of their :frequency field.
      The algorithm as as follows:
       (a) If the queue is of length one, it's done. The tree is the one element in the queue.
       (b) Otherwise, take the first two elements in the queue, and make a tree from them consisting of
           a branch node with the first element as its left and the second element as the right child.
           The :frequency field of the new tree is the sum of the frequencies of the two elements.
       (c) Insert the newly created tree into the rest of the queue (without the first two elements) according
           to its :frequency field (use insert-into-queue for this).
       (d) Call create-tree on the queue resulting from (c)."

     [Q]

     {
         :pre [
             (is-sorted-by? :frequency Q)
         ]
     }

   (if(= 1 (count Q))
     (first Q)
     (create-tree (insert-into-queue (make-branch (first Q) (second Q)) (drop 2 Q))))

     ;; hint: you could use the function make-branch from edaa40.huffutil. Also take a look at first, second, and drop.
 )

 (test? "create-tree 1" (create-tree ConstantSequenceInitialQueue) ConstantSequenceTree)
 (test? "create-tree 2" (create-tree SimpleSequenceInitialQueue) SimpleSequenceTree)
 (test? "create-tree 3" (:frequency (create-tree TextBytesInitialQueue)) 595248)


(declare huffman-tree)

 (defn huffman-tree
     "Create a Huffman tree from a sequence of symbols.
      The following steps have to be taken:
       (a) Compute the frequencies of symbols in S.
       (b) Create a list of leaf nodes for each symbol. Each leaf node includes the symbol's frequency.
       (c) Sort that list in order of ascending frequency. This is the initial queue.
       (d) Call create-tree on this sorted list of leaf nodes."

     [S]

   (let [ F (frequencies S) ]
     (create-tree(vec(sort-by :frequency (map #(make-leaf % (F %)) (keys F)))))
     )

     ;; hint: my solution uses frequencies, map, keys and from edaa40.huffutil the function make-leaf.
     ;; hint two: take a look at the definition of TextBytesInitialQueue.

 )

 (test? "huffman-tree 1" (huffman-tree ConstantSequence) ConstantSequenceTree)
 (test? "huffman-tree 2" (huffman-tree SimpleSequence) SimpleSequenceTree)

;;
;; creating a Huffman code map from a tree
;;

;; you might need to define other functions used by "huffman-codes' in this place
(defn huffman-conj-paths
  "dir is 0 if child is left, 1 if it is right"
  [node dir]

  (map #(vector % (conj (node %) dir)) (keys node))
)


(declare huffman-codes)

 (defn huffman-codes
     "Given a Huffman tree, compute the Huffman codes for each symbol in it.
     Returns a map mapping each symbol to a sequence of bits (0 or 1)."
     [T]

   (cond (isbranch? T)
    (let [left (huffman-codes (:left T)) right (huffman-codes (:right T))]
      (into {} (concat (huffman-conj-paths left 0) (huffman-conj-paths right 1)))
      )
    (isleaf? T)
         {(:value T) '()}
    :else
    (assert false "Kind not defined")
    )


      ;; hint: for building the map, take a look at the function into --- my solutions both look like this:
      ;;       (into {} ...)
      ;; they also both involve defining other functions, for computing all symbols in the tree, for
      ;; finding the bit string for a symbol in the tree, or other things...
 )


 (test? "huffman-codes 1" (huffman-codes ConstantSequenceTree) ConstantSequenceCodes)
 (test? "huffman-codes 2" (huffman-codes SimpleSequenceTree) SimpleSequenceCodes)


;;
;;  Huffman encoding a byte sequence
;;

(declare huffman-encode)

 (defn huffman-encode
     "Produces the complete Huffman code for a sequence of bytes (0 to 255).
     A Huffman code is represented as a map containing a Huffman tree, the length of the original sequence, and the sequence of bits encoding it."
     [S]

   (let[ tree(huffman-tree S) code(huffman-codes tree)]
     {:tree tree, :length (count S), :bits (mapcat #(code %) S)})

     ;; hint: take a look at the function mapcat; I also used huffman-tree and huffman-codes
 )

 (test? "huffman-encode 1" (huffman-encode ConstantSequence) ConstantSequenceHuffmanCode)
 (test? "huffman-encode 2" (huffman-encode SimpleSequence) SimpleSequenceHuffmanCode)
 (test? "huffman-encode 3" (count (:bits (huffman-encode TextBytes))) 2661055)

;;
;;  Huffman decoding a bit sequence
;;

(declare decode-symbol)

 (defn decode-symbol
     "Uses the beginning of the provided bit sequence to decode the next symbol based on the tree T.
     Returns a map with the decoded symbol in the :value field and the remaining bit sequence as :remaining-bits."

     [T bits]

     (if (isleaf? T)
       {:value (:value T) :remaining-bits bits}
       (decode-symbol ((if (= (first bits) 0) :left :right) T) (drop 1 bits))
       )
     ;; hint: this is pretty straightforward recursive descent --- you might want to use isleaf? at some point

 )

 (test? "decode-symbol 1" (decode-symbol SimpleSequenceTree SimpleSequenceBits) {:value :a :remaining-bits (drop 1 SimpleSequenceBits)})
 (test? "decode-symbol 2" (decode-symbol SimpleSequenceTree SimpleSequenceBits) {:value :a :remaining-bits (drop 1 SimpleSequenceBits)})
 (test? "decode-symbol 3" (decode-symbol SimpleSequenceTree (drop 3 SimpleSequenceBits)) {:value :b :remaining-bits (drop 5 SimpleSequenceBits)})
 (test? "decode-symbol 4" (decode-symbol SimpleSequenceTree (drop 7 SimpleSequenceBits)) {:value :c :remaining-bits (drop 10 SimpleSequenceBits)})


(defn huffman-decode
    "Decode a Huffman code (comprising a Huffman tree, a length, and bits representing a Huffman encoding) into a sequence of bytes of the specified length."

    [H]
    
    (loop
        [
            N       (:length H)
            bits    (:bits H)
            S       []
        ]

        (if (= N 0)
            S
            (let
                [ {v :value rbits :remaining-bits} (decode-symbol (:tree H) bits) ]
                
                (recur (dec N) rbits (conj S v))
            )
        )
    )
)

;; when you are done with the previous tests...

 (test? "huffman-decode 1" (huffman-decode (huffman-encode ConstantSequence)) ConstantSequence)
 (test? "huffman-decode 2" (huffman-decode (huffman-encode SimpleSequence)) SimpleSequence)
 (test? "huffman-decode 3" (huffman-decode (huffman-encode TextBytes)) TextBytes)



;;
;; Huffman file compression and decompression
;;
;; You can use these to try out the coding and decoding on files. It operates on bytes as symbols.
;;

(defn huffman-compress
    "Compresses a file using a Huffman code. Stores the complete code (incl. tree and original length) along with the bits."
    [infile outfile]
    
    (let
        [
            in-data     (slurp-bytes infile)
            h           (huffman-encode in-data)
            out-data    (create-huffman-bytes h)
        ]
        
        (spit-bytes outfile out-data)
    )
)
            
(defn huffman-decompress
    "Decompresses a file containing a complete Huffman code into the original."
    [infile outfile]
    
    (let
        [
            in-data     (slurp-bytes infile)
            h           (parse-huffman-bytes in-data)
            out-data    (huffman-decode h)
        ]
        
        (spit-bytes outfile out-data)
    )
)
            



