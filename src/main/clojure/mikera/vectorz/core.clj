(ns mikera.vectorz.core
  (:import [mikera.vectorz AVector Vectorz Vector])
  (:refer-clojure :exclude [+ - * / vec vec? vector subvec]))


(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(defmacro error
  "Throws a vectorz error with the provided message(s)"
  ([& vals]
    `(throw (mikera.vectorz.util.VectorzException. (str ~@vals)))))


(defn clone
  "Creates a (mutable) clone of a vector. May not be exactly the same class as the original vector."
  (^AVector [^AVector v]
    (.clone v)))


(defn length
  "Returns the length (number of components) of a vector"
  (^long [^AVector v]
    (.length v)))

(defn vec?
  "Returns true if v is a vector (instance of mikera.vectorz.AVector)"
  ([v]
    (instance? mikera.vectorz.AVector v)))

;; vector constructors


(defn of 
  "Creates a vector from its numerical components"
  ([& xs]
    (let [ss (seq xs)
           len (int (count ss))
           v (Vectorz/newVector len)]
       (loop [i (int 0) ss ss]
         (if ss
           (do
             (.set v i (double (first ss)))
             (recur (inc i) (next ss)))
           v)))))

(defn vec
  "Creates a vector from a collection or sequence"
  ([coll]
  (cond 
    (vec? coll) (clone coll)
    (instance? java.util.List coll) (Vectorz/create ^java.util.List coll)
    (sequential? coll) (apply of coll)
    (instance? java.lang.Iterable coll) (Vectorz/create ^java.lang.Iterable coll)
    :else (error "Can't create vector from: " (class coll)))))

(defn vector 
  "Creates a vector from zero or more numerical components."
  (^AVector [& xs]
    (vec xs)))


(defn create-length
  "Creates a vector of a specified length. Will use optimised primitive vectors for small lengths"
  (^AVector [len]
    (Vectorz/newVector (int len))))

(defn subvec
  "Returns a subvector of a vector. The subvector is a reference (i.e can be sed to modify the original vector)" 
  (^AVector [^AVector v start end]
    (.subVector v (int start) (int end))))


;; =====================================
;; In-place operations

(defn add
  "Add a vector to another (in-place)"
  (^AVector [^AVector dest ^AVector source]
    (.add dest source)
    dest))

(defn sub
  "Subtract a vector from another (in-place)"
  (^AVector [^AVector dest ^AVector source]
    (.sub dest source)
    dest))

(defn mul
  "Multiply a vector with another vector or scalar (in-place)"
  (^AVector [^AVector dest source]
    (if (number? source) 
      (.multiply dest (double source))
      (.multiply dest ^AVector source))
    dest))

(defn div
  "Divide a vector by another vector or scalar (in-place)"
  (^AVector [^AVector dest source]
    (if (number? source) 
      (.divide dest (double source))
      (.divide dest ^AVector source))
    dest))

;; =====================================
;; Arithmetic functions and operators

(defn approx=
  "Returns a boolean indicating whether the two vectors are approximately equal, to an optional tolerance" 
  ([^AVector a ^AVector b]
    (.epsilonEquals a b))
  ([^AVector a ^AVector b epsilon]
    (.epsilonEquals a b (double epsilon))))

(defn dot
  "Compute the dot product of two vectors"
  (^double [^AVector a ^AVector b]
    (.dotProduct a b)))

(defn + 
  "Add one or more vectors"
  (^AVector [^AVector a] (clone a))
  (^AVector [^AVector a ^AVector b] 
    (let [r (clone a)]
      (.add r b)
      r))
  (^AVector [^AVector a ^AVector b & vs] 
    (let [r (clone a)]
      (.add r b)
      (doseq [^mikera.vectorz.AVector v vs]
        (.add r v))
      r)))

(defn - 
  "Substract one or more vectors"
  (^AVector [^AVector a] (clone a))
  (^AVector [^AVector a ^AVector b] 
    (let [r (clone a)]
      (.sub r b)
      r))
  (^AVector [^AVector a ^AVector b & vs] 
    (let [r (- a b)]
       (doseq [^AVector v vs]
        (.sub r v))
      r)))

(defn * 
  "Multiply one or more vectors"
  (^AVector [^AVector a] (clone a))
  (^AVector [^AVector a ^AVector b] 
    (let [r (clone a)]
      (.multiply r b)
      r))
  (^AVector  [^AVector a ^AVector b & vs] 
    (let [r (* a b)]
      (doseq [^AVector v vs]
        (.multiply r v))
      r)))

(defn divide 
  "Divide one or more vectors"
  (^AVector [^AVector a] (clone a))
  (^AVector [^AVector a ^AVector b] 
    (let [ r  (clone a)]
      (.divide ^AVector r ^AVector b)
      r))
  (^AVector [^AVector a ^AVector b & vs] 
    (let [^AVector r (divide a b)]
      (doseq [^AVector v vs]
        (.divide r v))
      r)))