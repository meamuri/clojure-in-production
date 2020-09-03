(ns auth
  (:require [store :refer [retrieve-user-from-store users-store]]
            [compojure.core :refer [defroutes POST]]
            [taoensso.timbre :as timbre]))

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

(defn wrap-auth-user-only [handler]
  (fn [request]
    (if (:user request)
      (handler request)
      {:status 403
       :body "Unauthorized"})))

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
