(ns core
  (:require [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [taoensso.timbre :as timbre]))

(defn app-naked [request]
  (let [who (get-in request [:params :who])]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (format "Hello %s" who)}))

;; middleware for actual parameters logging
(defn with-log-parameters [handler]
  (fn [request]
    (let [params (get request :params)]
      (timbre/info params)) ;; Fixme: twice logging - with actual parameter and then with empty map
    (handler request)))

(def wrap-params* (comp wrap-params wrap-keyword-params))

(def app (-> app-naked
             with-log-parameters ;; this custom middleware will be called before app-naked but after `wrap-params*`
             wrap-params*))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
