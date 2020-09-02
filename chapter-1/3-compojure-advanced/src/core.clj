(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET ANY POST defroutes context]]))

(defn not-found [_]
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body "<head></head><body>not found<body>"})

(defn order-view [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<head></head><body>order view after editing<body>"})

(defn concrete-order-view [request]
  (let [{:keys [params]} request
        order-id (get-in params [:id])]
    (print params)
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body 
     (format "<head></head><body>order %s <body>" order-id)}))

(defn order-form [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<head></head><body>the form<body>"})

(defn order-change [request]
  (let [order-id (get-in request [:params :id])]
    {:status 302
     :headers {"Location" (format "/content/order/%s/view" order-id)}}))

(defroutes app
  (context "/content/order/:id" [order-id]
    (GET "/view" request (order-view request))
    (context "/edit" []
      (GET "/" request (order-form request))
      (POST "/" request (order-change request)))
    concrete-order-view)
  (ANY "/health" _ "ok")
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
