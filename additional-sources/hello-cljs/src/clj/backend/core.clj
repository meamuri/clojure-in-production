(ns backend.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [hiccup.core :refer [html5 head include-js]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to todo-sing"]
   [:p "please wait while Figwheel is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))

(defn app [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (page)})

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
