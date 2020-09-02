(ns core
  (:require [clojure.walk :refer [keywordize-keys stringify-keys]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response, wrap-json-body]]
            [compojure.core :refer [GET POST defroutes context wrap-routes]]
            [taoensso.timbre :as timbre]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.cookies :refer [wrap-cookies]]))

(def users-store (atom {:1 {:name "corjure" :email "corjure@corz.com" :password "woof!"}
                        :2 {:name "meowch" :email "meowch@meow.com" :password "meow"}}))

(defn retrieve-user-from-store [user-id] 
  (get @users-store user-id))


(def authorized-path ["authorized" :value])

(defn wrap-current-user [handler]
  (fn [request]
    (timbre/info (format "Check %s" (get-in request [:headers])))
    (let [{:keys [headers]} request
          {:keys [auth]} headers
          user-id (-> auth keyword)
          user (when user-id 
                 ;; retrieving is long operation if that communicates with db.
                 ;; `when `condition prevents db query if user-id is not present
                 (retrieve-user-from-store user-id))]
      (-> request
          (assoc :user user)
          handler))))

(defn wrap-headers-kw [handler]
  (fn [request]
    (-> request
        (update :headers keywordize-keys)
        handler
        (update :headers stringify-keys))))

(defn wrap-auth-user-only [handler]
  (fn [request]
    (if (:user request)
      (handler request)
      {:status 403       
       :body "Unauthorized"})))

(defn simple-handler [request]
  (timbre/info (format "user %s requests simple data" (:user request)))
  {:status 200
   :body {:data "just simple data"}})


(defroutes api
  (GET "/simple" request (simple-handler request)))

(defn login [request]
  (let [{:keys [cookies body]} request        
        password (get body "password")
        id (get body "id")
        user-id (-> id str keyword)
        user (get @users-store user-id)
        correct-pass? (= password (:password user))]
    (timbre/info (format "User %s try to login. Authenticated: %s" user correct-pass?))
    (if correct-pass?
      {:status 200
       :cookies (assoc-in cookies authorized-path user-id)
       :body "ok"}
      {:status 403       
       :body {:result "no"}})))

(defn logout [request]
  (let [{:keys [cookies]} request
        cookies* (assoc-in cookies authorized-path nil)]
    {:status 200
     :cookies cookies*
     :body {:result "logout completed"}}))

(defroutes login-app
  (POST "/sign-in" request (login request))
  (POST "/sign-out" request (logout request)))

(defroutes app*
  (context "/login" []
    login-app)
  (context "/api" []
    (wrap-routes api (comp wrap-current-user wrap-auth-user-only))))

(def app (-> app*
             wrap-headers-kw
             wrap-keyword-params
             wrap-json-response
             wrap-json-body
             wrap-cookies))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(timbre/info "Thank you for reading the file")
(comment
  (start)
  (stop))
