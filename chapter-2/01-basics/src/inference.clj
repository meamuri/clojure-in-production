(ns inference
  (:require [clojure.spec.alpha :as s]
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
