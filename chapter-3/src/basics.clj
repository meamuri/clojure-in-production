(ns basics)

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
