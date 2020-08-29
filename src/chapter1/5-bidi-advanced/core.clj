(ns core
  (:require [bidi.bidi :as bidi]
            [ring.adapter.jetty :refer [run-jetty]]
            [taoensso.timbre :as timbre]
            [ring.middleware.json :refer [wrap-json-body]]))

(def routes 
  [["/content/order/" :id] {"/view" {:get :page-view}
                            "/edit" {:get :page-form
                                     :post :page-save}}])
(def orders 
  (atom {:125 {:stuff ["iphone" "macbook"]
               :price 2400
               :delivery :by-address}})) 

(defn get-order-by-id [order-id]
  ((keyword order-id) @orders))

(defn render-order-view [order]
  (format "<header></header><body>the order is %s</body>" order))

(def page-404
  {:status 404
   :headers {"content-type" "text/html"}
   :body "Not found"})

(defmulti multi-handler
  :handler)

(defmethod multi-handler :page-view
  [request]
  (if-let [order (some-> request
                         :route-params
                         :id
                         get-order-by-id)]
    {:status 200
     :headers {"content-type" "text/html"}
     :body (render-order-view order)}
    page-404))

(defmethod multi-handler :page-save
  [request]
  (let [{:keys [body route-params]} request
        order-id (get route-params :id)
        id (keyword order-id)]
    (timbre/info request)
    (reset! orders (assoc @orders id body))
    {:status 302
     :headers {"Location" (format "/content/order/%s/view" order-id)}}))

(defn wrap-handler [handler]
  (fn [request]
    (let [{:keys [uri]} request
          request* (bidi/match-route* routes uri request)]
      (handler request*))))
(def base-app (wrap-handler multi-handler))
(def app (wrap-json-body base-app {:keywords? true}))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment  
  (start)
  (stop))
