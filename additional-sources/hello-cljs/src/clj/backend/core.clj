(ns backend.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [hiccup.page :refer [html5 include-js include-css]]
            [hiccup.core :refer [html]]
            [ring.middleware.resource :refer [wrap-resource]]))

(def mount-target
  (html [:div#app
         [:h2 "Welcome to app"]
         [:p "please wait while Figwheel is waking up ..."]
         [:p "(Check the js console for hints if nothing exciting happens.)"]]))

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "/css/site.css")])

(defn page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))

(defn app* [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (page)})

(def app (-> app*
             (wrap-resource "public")))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
