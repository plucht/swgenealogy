(ns genealogy.core
  (:gen-class)
  (:require [clojure.core.logic.pldb :as pldb]
            [clojure.core.logic :as logic]))


(pldb/db-rel parent p c) 
(pldb/db-rel male p)
(pldb/db-rel female p)

; todo: import functions into namespace (get rid of ns prefix)
; todo: global database
; todo: higher order functions
; todo: LotR data? 

;; (def facts (apply pldb/db-facts pldb/empty-db 
;;   ; (macroexpand '(ed (:anakin :padme (:luke :leia))))
;;   (eded '(:anakin :padme (:luke :leia)))
;; 
;;   ; [
;;   ;   [parent :shmi :anakin]
;;   ;   [parent :anakin :luke]
;;   ;   [parent :padme :luke]
;;   ;   [parent :anakin :leia]
;;   ;   [parent :padme :leia]
;;   ; ]
;;   ))

(def facts (pldb/db-facts pldb/empty-db 
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

(defn parents-of [db person] 
  (pldb/with-db db 
    (logic/run* [q] (parent q person))))

(defn children-of [db person] 
  (pldb/with-db db
    (logic/run* [q] (parent person q))))

(defn father [x y] 
  (logic/fresh [] 
    (parent x y)
    (male x)))

(defn father-of [db person]
  (pldb/with-db db 
    (logic/run* [q] 
      (father q person))))

(defn mother [x y] 
  (logic/fresh [] 
    (parent x y)
    (female x)))

(defn mother-of [db person]
  (pldb/with-db db 
    (logic/run* [q] 
      (mother q person))))

(defn sister [x y] 
  (logic/fresh [p] 
    (parent p x)
    (parent p y)
    (female p) ; hotfix: only use children of mother -> remove duplicated children
    (female x)
    (logic/!= x y)))

(defn sister-of [db person]
  (pldb/with-db db 
    (logic/run* [q] 
      (sister q person))))

(defn brother [x y] 
  (logic/fresh [p]
    (parent p x)
    (parent p y)
    (female p) ; hotfix: only use children of mother -> remove duplicated children
    (male x)
    (logic/!= x y)))

(defn brother-of [db person] 
  (pldb/with-db db 
    (logic/run* [q] 
      (brother q person))))

(defn grandparent [x y]
  (logic/fresh [z]
         (parent x z)
         (parent z y)))

(defn grandparent-of [db person] 
  (pldb/with-db db 
    (logic/run* [q] 
      (grandparent q person))))

(defn predecessor [x z] 
  (logic/conde 
    [(parent x z)]
    [(logic/fresh [y]
      (parent x y)
      (predecessor y z))]))

(defn predecessor-of [db person] 
  (pldb/with-db db 
    (logic/run* [q]
      (predecessor q person))))

(defn is-predecessor-of [db predecessor person] 
  (some? 
    (some #{predecessor} (predecessor-of db person))))

(defn -main [& args]
  (println "Who are the parents of :luke?")
  (println (parents-of facts :luke))
  (println "Who are the children of :anakin?")
  (println (children-of facts :anakin))
  (println "Who are the children of :padme?")
  (println (children-of facts :padme))
  (println "Who is the mother of :luke?")
  (println (mother-of facts :luke))
  (println "Who is the mother of :anakin?")
  (println (mother-of facts :anakin))
  (println "Who is the father of :luke?")
  (println (father-of facts :luke))
  (println "Who is the father of :anakin?")
  (println (father-of facts :anakin))
  (println "Who is the sister of :luke?")
  (println (sister-of facts :luke))
  (println "Who is the brother of :leia?")
  (println (brother-of facts :leia))
  (println "Who is the grandparent of :leia?")
  (println (grandparent-of facts :leia))
  (println "Who is the predecessor of :leia?")
  (println (predecessor-of facts :leia))
  (println "is :shmi a predecessor of :leia?")
  (println (is-predecessor-of facts :shmi :leia))
  (println "Who is the sister of :leia?")
  (println (sister-of facts :leia))
)