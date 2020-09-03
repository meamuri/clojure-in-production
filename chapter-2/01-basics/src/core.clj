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
