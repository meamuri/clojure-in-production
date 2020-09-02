(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET defroutes]]))


(defn page-index [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Learning clojure"})

(defn page-hello [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hi there!"})

(defn not-found [_]
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not found"})

(defroutes app
  (GET "/" request (page-index request))
  (GET "/hello" request (page-hello request))
  not-found)

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
