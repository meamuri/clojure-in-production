(ns core
  (:require [clojure.spec.alpha :as s]))

(s/def ::string string?)

(comment
  (s/valid? ::string 1)       ;; => false
  (s/valid? ::string "test")) ;; => true
