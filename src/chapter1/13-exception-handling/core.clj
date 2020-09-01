(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET defroutes]]            
            [taoensso.timbre :as timbre]))

(defn wrap-exception-handling [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable e 
        (let [{:keys [uri request-method]} request]
          (timbre/error "Error was thrown:" uri request-method e)
          {:status 500
           :headers {"Content-Type" "text/plain"}
           :body "Sad message about server exception"})))))

(defn danger-handler [request]
  (throw (Exception. "Woooof")))

(defroutes app*
  (GET "/green-zone" _ "greeeen")
  (GET "/danger-zone" request (danger-handler request)))

(def app (-> app*
             wrap-exception-handling))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
