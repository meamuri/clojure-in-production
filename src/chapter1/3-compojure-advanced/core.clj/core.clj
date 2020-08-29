(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET defroutes context]]))

(defn not-found [_]
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body "<head></head><body>not found<body>"})

(defn order-view [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<head></head><body>order<body>"})

(defn concret-order-view [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<head></head><body>order<body>"})

(defn order-form [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<head></head><body>the form<body>"})

(defroutes app
  (context "/content/order/:id" [order-id]
    (GET "/view" request (order-view request))
    (context "/edit" []
      (GET "/" request (order-form request)))
    concret-order-view)
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
