(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [GET POST defroutes context]]))

(def wrap-params+ (comp wrap-params wrap-keyword-params))

(def users (atom {:1 {:name "woofer"
                      :email "woofer@gmail.com"}}
                 :2 {:name "meower"
                     :email "meower@gmail.com"}))

(defn get-all-users [_]
  {:status 200
   :body @users})

(defn handle-user-view [user-id] 
  (fn [request] 
    (if-let [user (get-in @users [user-id])]
      {:status 200
       :body user}
      {:status 404
       :body {:error-code "MISSING_USER"
              :error-message "User not found"}})))

(defn handle-user-change [request]
  {})

(defroutes app-routes
  (context "/users" []
    (GET "/" request (get-all-users request))
    (context "/:id" [user-id]
      (GET "/" request ((handle-user-view (keyword user-id)) request))
      (POST "/" request ((handle-user-change (keyword user-id)) request)))))


(def app (-> app-routes             
             wrap-params+
             wrap-json-body
             wrap-json-response))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
