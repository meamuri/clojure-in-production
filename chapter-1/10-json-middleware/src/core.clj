(ns core
  (:require [handlers :refer [get-all-users handle-user-change handle-get-user]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [GET POST defroutes context]]
            [taoensso.timbre :as timbre]))

(def wrap-params+ (comp wrap-params wrap-keyword-params))

(defroutes app-routes
  (context "/users" []
    (GET "/" request (get-all-users request))
    (context "/:id" [_]
      (GET "/" request (handle-get-user request))
      (POST "/" request (handle-user-change request)))))


(def app (-> app-routes             
             wrap-params+
             (wrap-json-body {:keywords? true})
             wrap-json-response))

(def server (atom nil))

(defn start []
  (timbre/info "Starting server for clojure rock")
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
