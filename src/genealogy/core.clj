(ns genealogy.core
  (:gen-class)
  (:require [clojure.core.logic.pldb :refer [db db-rel empty-db with-db]]
            [clojure.core.logic :as logic :refer [run* fresh !=]]))

; todo: LotR data? 
(db-rel parent p c) 
(db-rel male p)
(db-rel female p)

(def facts (db
  [parent :shmi :anakin]
  [parent :anakin :luke]
  [parent :padme :luke]
  [parent :anakin :leia]
  [parent :padme :leia]
  [male :anakin]
  [male :luke]
  [female :shmi]
  [female :padme]
  [female :leia]))

(defn whois 
  ([f p1] 
    (with-db facts (run* [q] (f q p1))))
  ([f p1 p2] 
    (with-db facts (run* [q] (f q p1 p2)))))

(defn rawis [p1 predicate p2] 
  (some? 
    (some #{p1} (whois predicate p2))))

(defn is [p1 predicate p2]
  (if rawis "Yes" "No"))

(defn parentof [q person] 
  (parent q person))

(defn childof [q person] 
  (parent person q))

(defn father [x y] 
  (fresh [] 
    (parent x y)
    (male x)))

(defn mother [x y] 
  (fresh [] 
    (parent x y)
    (female x)))

(defn offspringof [q f m] 
  (fresh [] 
    (parent f q)
    (parent m q)
    (!= f m)))

(defn sister [x y] 
  (fresh [p] 
    (parent p x)
    (parent p y)
    (female p) ; hotfix: only use children of mother -> remove duplicated children
    (female x)
    (!= x y)))

(defn brother [x y] 
  (fresh [p]
    (parent p x)
    (parent p y)
    (female p) ; hotfix: only use children of mother -> remove duplicated children
    (male x)
    (!= x y)))

(defn grandparent [x y]
  (fresh [z]
         (parent x z)
         (parent z y)))

(defn predecessorof [x z] 
  (logic/conde 
    [(parent x z)]
    [(fresh [y]
      (parent x y)
      (predecessorof y z))]))

(defn -main [& args]
  (println "Who are the parents of :luke?")
  (println (whois parentof :luke))
  
  (println "Who are the children of :anakin?")
  (println (whois childof :anakin))
  
  (println "Who is the mother of :luke?")
  (println (whois mother :luke))

  (println "Who is the mother of :anakin?")
  (println (whois mother :anakin))
  
  (println "Who is the father of :luke?")
  (println (whois father :luke))
  (println "Who is the father of :anakin?")
  (println (whois father :anakin))
  
  (println "Who is the sister of :luke?")
  (println (whois sister :luke))
  
  (println "Who is the brother of :leia?")
  (println (whois brother :leia))

  (println "Who is the grandparent of :leia?")
  (println (whois grandparent :leia))
  (println "Is :shmi a grandparent of :luke?")
  (println (is :shmi grandparent :luke))

  (println "Who is the predecessor of :leia?")
  (println (whois predecessorof :leia))

  (println "Who is the sister of :leia?")
  (println (whois sister :leia))

  (println "is :shmi a predecessor of :leia?")
  (println (is :shmi predecessorof :leia))

  (println "Show offspring of :anakin and :padme.")
  (println (whois offspringof :anakin :padme))

  (println "Show offsping of :anakin and :shmi.")
  (println (whois offspringof :anakin :shmi)) ; let's hope this is empty
)