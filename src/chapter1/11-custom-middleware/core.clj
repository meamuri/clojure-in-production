(ns core
  (:require [clojure.walk :refer [keywordize-keys stringify-keys]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response]]
            [compojure.core :refer [GET defroutes context wrap-routes]]
            [taoensso.timbre :as timbre])
  (:import java.util.UUID))

(defn wrap-headers-kw [handler]
  (fn [request]
    (-> request
        (update :headers keywordize-keys)
        handler
        (update :headers stringify-keys))))

(defn show-host [request]
  (let [{:keys [headers]} request
        {:keys [host]} headers]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (format "<header></header><body>Actual host is %s</body>" host)}))

(defn wrap-request-id [handler]
  (fn [request]
    (let [uuid (or (get-in request [:headers :x-request-id])
                   (str (UUID/randomUUID)))]
      (-> request
          (assoc-in [:headers :x-request-id] uuid)
          (assoc :request-id uuid)
          handler          
          (assoc :request-id uuid)
          (assoc-in [:headers :x-request-id] uuid)))))

(defn show-request-id [request]
  (let [uuid (:request-id request)]
    (timbre/info (format "Request id is %s" uuid))
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body {:some-info "of course"}}))

(defroutes request-id-example-app 
  (GET "/show-request-id" request (show-request-id request)))

(defroutes host-example-app
  (GET "/show-host" request (show-host request)))

(defroutes app*
  (context "/host" []
    host-example-app)
  (context "/api" [] 
    (wrap-routes request-id-example-app 
                 (comp wrap-request-id wrap-json-response))))

(def app (-> app*             
             wrap-headers-kw))

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
