(ns basics
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]))

(defn weird-arithments []
  (try
    (/ 1 0)
    (catch ArithmeticException _
      (println "Weird arithmetics"))))

(comment
  (weird-arithments))

(defn show-message-example []
  (try
    (/ 1 0)
    (catch ArithmeticException e
      (println (ex-message e)))))

(comment
  (show-message-example))

(defn multiple-catch []
  (try
    (/ 1 nil)
    (catch ArithmeticException e
      (println "Weird arithmetics"))
    (catch NullPointerException e
      (println "You've got a null value"))))

(comment
  (multiple-catch))

;; you can catch everything with `(catch Throwable e ...)`

(defn throw-e []
  (let [e (new Exception "brand new exception")]
    (throw e)))

(comment
  (throw-e))

(defn add [a b]
  (if (and a b)
    (+ a b)
    (let [message (format "Value error: a: %s, b: %s" a b)]
      (throw (new Exception message)))))

(comment 
  (add 1 nil))

(defn get-user [user-id] 
  (throw (ex-info "Cannot fetch user"
                  {:user-id user-id
                   :http-status 404
                   :http-method "GET"
                   :http-url "https://the-url.com"})))

(defn do-call []
  (try
    (get-user 42)
    (catch clojure.lang.ExceptionInfo e
      (let [{:keys [http-method http-url]} (ex-data e)]
        (format "Http error: %s %s" http-method http-url)))))

(comment
  (do-call))

(s/def ::data (s/coll-of int?))

(defn throw-with-explain [data]
  (when-let [explain (s/explain-data ::data data)]
    (throw (ex-info "Some item is not an integer"
                    {:explain explain})))
  "All right")

(comment
  (throw-with-explain [1 2 nil])
  (throw-with-explain [1 2 12])
  (try 
    (throw-with-explain [nil])
    (catch Exception e
      (ex-data e))))

(defn devide [a b]
  (try 
    (/ a b)
    (catch ArithmeticException e
      (throw (ex-info
              "Calculation error"
              {:a a :b b}
              e)))))

(defn check-devide []
  (try 
    (devide 1 0)
    (catch Exception e
      (println (ex-message e))
      (println (ex-message (ex-cause e))))))

(comment 
  (check-devide))

(defn ex-chain [e]
  (loop [e e
         result []]
    (if (nil? e)
      result
      (recur (ex-cause e) (conj result e)))))

(def constant-e
  (ex-info 
   "Get user info error"
   {:user-id 42}
   (ex-info "Auth error"
            {:token "...."}
            (ex-info "HTTP error"
                     {:method "POST"
                      :url "https://api.site.com"}))))

(defn flat-error-messages []
  (map ex-message (ex-chain constant-e)))

(comment
  (flat-error-messages))

(defn print-line-by-line []
  (doseq [e (ex-chain constant-e)]
          (-> e ex-message println)))

(comment
  (print-line-by-line))

(defn ex-chain* []
  (take-while some? (iterate ex-cause constant-e)))

(comment
  (ex-chain*))

(comment
  (log/info "First one printing")
  (log/error constant-e "HTTP error"))

(defn ex-print
  [^Throwable e]
  (let [indent "   "]
    (doseq [e (ex-chain e)]
      (println (-> e class .getCanonicalName))
      (print indent)
      (println (ex-message e))
      (when-let [data (ex-data e)]
        (print indent)
        (clojure.pprint/pprint data)))))

(comment
  (ex-print constant-e))

(defn log-error 
  [^Throwable e & [^String message]]
  (log/error 
   (with-out-str
     (println (or message "Error"))
     (ex-print e))))

(comment
  (log-error constant-e)
  (log-error constant-e "HTTP Error 500"))

;; Task:
;; Add to log-error possibility to call with pattern such as: 
;; (log-error err "Cannot find user %s, status %s" 42 404)