(ns api
  (:require [compojure.core :refer [defroutes GET]]
            [taoensso.timbre :as timbre]))


(defn simple-handler [request]
  (timbre/info (format "user %s requests simple data" (:user request)))
  {:status 200
   :body {:data "just simple data"}})

(defroutes api
  (GET "/simple" request (simple-handler request)))
