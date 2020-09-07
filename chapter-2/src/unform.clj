(ns unform
  (:require [clojure.spec.alpha :as s]))

(s/def ::ne-string
  (s/and string? not-empty))

(s/def ::smart-port
  (s/or :string ::->int :num int?))

(s/def :conn/port ::smart-port)

(s/def ::conn (s/keys :req-un [:conn/port]))

(comment
  (s/unform ::conn {:port [:num 9090]})) ;; {:port 9090}


(s/def ::->int
  (s/and
   ::ne-string
   (s/conformer
    (fn [int]
      (Integer/parseInt int))
    identity)))

(comment
  (s/unform ::conn {:port [:string 8080]})
  (->> {:port 8000}
       (s/conform ::conn)
       (s/unform ::conn))
  (->> {:port "9090"}
       (s/conform ::conn)
       (s/unform ::conn))) ;; {:port 8000}

;; add unform to macrosg
(defmacro with-conformer
  [[bind] & body]
  `(s/conformer
    (fn [~bind]
      (try
        ~@body
        (catch Exception e#
          ::s/invalid)))
    identity))
