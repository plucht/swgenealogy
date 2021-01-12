(ns genealogy.core
  (:gen-class)
  (:require [clojure.core.logic.pldb :refer [db db-rel empty-db with-db]]
            [clojure.core.logic :refer [conde run* fresh !=]]))

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
  ([relation x] 
    (with-db facts (run* [q] (relation q x))))
  ([relation x y] 
    (with-db facts (run* [q] (relation q x y)))))

(defn rawis [x relation y] 
  (some? 
    (some #{x} (whois relation y))))

(defn is [x relation y]
  (if (rawis x relation y) "Yes" "No"))

(defn parentof [q x] 
  (parent q x))

(defn childof [q x] 
  (parent x q))

(defn fatherof [q x] 
  (fresh [] 
    (parent q x)
    (male q)))

(defn motherof [q x] 
  (fresh [] 
    (parent q x)
    (female q)))

(defn offspringof [q x y] 
  (fresh [] 
    (parent x q)
    (parent y q)
    (!= x y)))

(defn sisterof [q x] 
  (fresh [m] 
    (female q)
    (motherof m x)
    (motherof m q)
    (!= q x)))

(defn brotherof [q x]  
  (fresh [m]
    (male q)
    (motherof m x)
    (motherof m q)
    (!= q x)))

(defn grandparentof [q x]
  (fresh [p]
    (parent p x)
    (parent q p)))

(defn predecessorof [q x] 
  (conde 
    [(parent q x)]
    [(fresh [y]
      (parent q y)
      (predecessorof y x))]))

(defn what-relationship [q x] 
  (let [rel [parentof 
             childof
             fatherof
             motherof
             sisterof 
             brotherof 
             grandparentof 
             predecessorof]]
  (map #(second (re-find #"\$([^\@]+)\@" %)) ; find name of relationship
    (filter some? 
      (for [r rel] 
        (if (rawis q r x) (str r) nil))))))

(defn -main [& args]
  (println "Who are the parents of :luke?")
  (println (whois parentof :luke))
  
  (println "Who are the children of :anakin?")
  (println (whois childof :anakin))
  
  (println "Who is the mother of :luke?")
  (println (whois motherof :luke))

  (println "Who is the mother of :anakin?")
  (println (whois motherof :anakin))
  
  (println "Who is the father of :luke?")
  (println (whois fatherof :luke))
  (println "Who is the father of :anakin?")
  (println (whois fatherof :anakin))
  
  (println "Who is the sister of :luke?")
  (println (whois sisterof :luke))
  
  (println "Who is the brother of :leia?")
  (println (whois brotherof :leia))

  (println "Who is the grandparent of :leia?")
  (println (whois grandparentof :leia))
  (println "Is :shmi a grandparent of :luke?")
  (println (is :shmi grandparentof :luke))

  (println "Who is the predecessor of :leia?")
  (println (whois predecessorof :leia))

  (println "Who is the sister of :leia?")
  (println (whois sisterof :leia))

  (println "Is :shmi a predecessor of :leia?")
  (println (is :shmi predecessorof :leia))

  (println "Is :padme a grandparent of :leia?")
  (println (is :padme grandparentof :leia))
  (println "How is :padme related to :leia?")
  (println (what-relationship :padme :leia))

  (println "Show offspring of :anakin and :padme.")
  (println (whois offspringof :anakin :padme))

  (println "Show offsping of :anakin and :shmi.")
  (println (whois offspringof :anakin :shmi)) ; let's hope this is empty
)