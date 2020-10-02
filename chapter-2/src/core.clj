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
  ; null safety check because ::url spec has `string?` validation
  (s/valid? ::url nil)                  ;; => false
  (s/valid? ::url "test")               ;; => false
  (s/valid? ::url "http://peace.com"))  ;; => true

(s/def ::age
  (s/and int?
         #(<= 0 % 150)))

(comment
  ; null safety check because ::age spec has `int?` validation
  (s/valid? ::age nil)       ;; => false
  (s/valid? ::age "test")    ;; => false
  (s/valid? ::age 1))        ;; => true

;; Collections check

(s/def ::url-list
       (s/coll-of ::url))

(comment
  (s/valid? ::url-list ["http://programming.edu" "clojure.do"])          ;; false
  (s/valid? ::url-list ["http://programming.edu" "https://future.io"]))  ;; true

(s/def ::params
  (s/map-of keyword? string?))

(comment
  (s/valid? ::params {:woof "yes"}) ;; true
  (s/valid? ::params {:meow "yesyes!"}) ;; true
  (s/valid? ::params {:london-is-the-capital-of-great-britan true}) ;; false
  (s/valid? ::params "not a map"))  ;; false

(s/def :page/link ::url)
(s/def :page/description ::ne-string)

(s/def :page/status int?)

(s/def ::page
       (s/keys :req-un [:page/address
                        :page/description]
               :opt-un [:page/status]))

(comment
  (s/valid? ::page {:address "http://clojure.org"
                    :description "Starting page for Clojure"}) ;; true
  (s/valid? ::page {:address "http://clojure.org"
                    :description "Starting page for Clojure"
                    :status 200}) ;; true
  (s/valid? ::page {:address "http://clojure.org"
                    :description ""}))  ;; false
