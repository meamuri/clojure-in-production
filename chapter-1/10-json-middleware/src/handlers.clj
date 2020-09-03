(ns handlers
  (:require [store :refer [users]]
            [taoensso.timbre :as timbre]))


(defn get-all-users [_]
  {:status 200
   :body (vals @users)})

(defn handle-get-user [request]
  (let [user-id (-> request :params :id keyword)]
    (timbre/info (-> request :params)) ;; debug printing params
    (if-let [user (get @users user-id)]
      {:status 200
       :body user}
      {:status 404
       :body {:error-code "MISSING_USER"
              :error-message "User not found"}})))

(defn handle-user-change [request]
  (let [user-id (-> request :params :id keyword)]
    (if-let [_ (user-id @users)]
      {:status 409
       :body "Already exist"}
      (let [{:keys [body]} request
            {:keys [name email]} body
            user {:name name :email email}]
        (timbre/info (format "User for create is %s" body))
        (reset! users (assoc-in @users [user-id] user))
        {:status 201
         :body user}))))
