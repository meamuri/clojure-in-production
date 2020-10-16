(ns gears.core
  (:require [bidi.bidi :as bidi]
            [hiccup.core :refer [html]]
            [ring.adapter.jetty :refer [run-jetty]]
            [gears.html-gen :as hg]
            [ring.middleware.resource :refer [wrap-resource]]))

(def routes
  ["/" {"" :page-index
        "hello" :page-hello
        true :not-found}])

(defn wrap-handler [handler]
  (fn [request]
    (let [{:keys [uri]} request
          request* (bidi/match-route* routes uri request)]
      (handler request*))))

(def wrapped (wrap-handler identity))

(defmulti multi-handler
  :handler)

(defmethod multi-handler :page-index
  [request]
  {:status 200
   :headlers {"content-type" "text/html"}
   :body (hg/render (html [:h1 "The text" [:div "asgas"]]))})

(defmethod multi-handler :page-hello
  [request]
  {:status 200
   :headlers {"content-type" "text/plain"}
   :body "Hello bidi"})

(defmethod multi-handler :not-found
  [request]
  {:status 404
   :headlers {"content-type" "text/plain"}
   :body "Page not found"})

(def app (-> (wrap-handler multi-handler)
             (wrap-resource "public")))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (wrapped {:request-method :get :uri "/hello?foo=42"})
  (start)
  (stop))
