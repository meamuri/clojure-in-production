(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn handler [request] {:status 200})

;; Go to address localhost:8080/public.html for looking at resource serving result
(def app (wrap-resource handler "public"))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
