(ns core
  (:require [clojure.spec.alpha :as s]))

(s/def ::string string?)

(comment
  (s/valid? ::string 1)       ;; => false
  (s/valid? ::string "test")) ;; => true

(s/def ::ne-string
  (s/and string? not-empty))

(comment
  (s/valid? ::ne-string 1)       ;; => false
  (s/valid? ::ne-string "")      ;; => false
  (s/valid? ::ne-string "test")) ;; => true

(s/def ::url
  (s/and ::ne-string
       (partial re-matches #"(?i)^http(s?)://.*")))

(comment
  (s/valid? ::url nil)                  ;; => false
  (s/valid? ::url "test")               ;; => false
  (s/valid? ::url "http://peace.com"))  ;; => true

(s/def ::age
  (s/and int?
         #(<= 0 % 150)))

(comment
  (s/valid? ::age nil)       ;; => false
  (s/valid? ::age "test")    ;; => false
  (s/valid? ::age 1))        ;; => true
