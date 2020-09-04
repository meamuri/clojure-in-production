(ns inference
  (:require [clojure.string]
            [clojure.spec.alpha :as s]
            [clojure.instant :refer [read-instant-date]]))

(s/def ::ne-string
  (s/and string? not-empty))

(s/def ::->int_
  (s/conformer
   (fn [value]
     (try
      (Integer/parseInt value)
      (catch Exception _
        ::s/invalid)))))

(comment
  (s/conform ::->int_ "42")
  (s/conform ::->int_ "ab12"))

(s/def ::->int
       (s/and ::ne-string ::->int_))

(comment
  (s/conform ::->int "as")
  (s/conform ::->int "16"))

(s/def ::->date
  (s/and
   ::ne-string
   (s/conformer
    (fn [value]
      (try
        (read-instant-date value)
        (catch Exception _
          ::s/invalid))))))

(comment
  (s/conform ::->date "2020-09-01")
  (s/conform ::->date "2020-09-01T23:59:59"))

(def bits-map {"32" 32 "64" 64})

(s/def ::->bits
  (s/conformer
   #(get bits-map % ::s/invalid)))

(comment
  (s/conform ::->bits "33")   ;; invalid
  (s/conform ::->bits "32"))  ;; 32

(s/def ::->bool
  (s/and
   ::ne-string
   (s/conformer clojure.string/lower-case)
   (s/conformer 
    (fn [value]
      (case value
        ("true" "1" "on" "yes") true
        ("false" "0" "off" "no") false
        ::s/invalid)))))

(comment
  (s/conform ::->bool "true") ;; true
  (s/conform ::->bool "off")  ;; false
  (s/conform ::->bool true)   ;; invalid
  (s/conform ::->bool 0)      ;; invalid
  (s/conform ::->bool "1"))   ;; true

(s/def ::status_ #{"todo" "in_progress" "done"})

(comment
  (s/valid? ::status_ "assigned") ;; false
  (s/valid? ::status_ "done")     ;; true
  (s/valid? ::status_ nil))       ;; flase


(defn enum [& args]
  (let [args-set (set args)]
    (fn [value]
      (contains? args-set value))))

(s/def ::status
  (enum "todo" "in_progress" "done"))

(comment
  (s/valid? ::status "assigned") ;; false
  (s/valid? ::status "done")     ;; true
  (s/valid? ::status nil))       ;; false

