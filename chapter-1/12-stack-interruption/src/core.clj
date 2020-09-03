(ns core
  (:require [util :refer [wrap-headers-kw]]            
            [api :as api]
            [auth]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response, wrap-json-body]]
            [compojure.core :refer [defroutes context wrap-routes]]
            [taoensso.timbre :as timbre]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.cookies :refer [wrap-cookies]]))

(defroutes app*
  (context "/login" []
    auth/login-app)
  (context "/api" []
    (wrap-routes api/api (comp auth/wrap-current-user auth/wrap-auth-user-only))))

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
