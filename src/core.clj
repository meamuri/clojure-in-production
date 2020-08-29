(ns in-production.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET defroutes]]))

(defn bak-app [request] 
  (let [{:keys [uri request-method]} request]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (format "You requested %s %s"
                   (-> request-method name .toUpperCase)
                   uri)}))

(defn page-index [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Learning clojure"})

(defn page-hello [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hi there!"})

(defn not-found [request]
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not found"})

(defroutes app
  (GET "/" request (page-index request))
  (GET "/hello" request (page-hello request))
  not-found)

(def server (run-jetty app {:port 8080 :join? false}))

(comment
  (app {:uri "/woosh/content" :request-method :get})
  (.stop server))
