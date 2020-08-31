(ns core
  (:require [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn app-naked [request] 
  (let [who (get-in request [:params "who"])]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (format "Hello %s" who)}))

(def app (wrap-params app-naked))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
