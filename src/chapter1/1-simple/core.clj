(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn app [request]
  (let [{:keys [uri request-method]} request]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (format "You requested %s %s"
                   (-> request-method name .toUpperCase)
                       uri)}))

(def server
  (run-jetty app {:port 8080 :join? false}))

(defn stop [] 
  (.stop server))

(comment 
  (stop))

