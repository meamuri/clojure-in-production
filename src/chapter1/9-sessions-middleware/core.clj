(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.session :refer [wrap-session]]))

(defn page-counter [request] 
  (let [{:keys [session]} request
        session* (update session :counter (fnil inc 0))]
    {:status 200
     :session session*
     :body (format "Seen %s times" (:counter session*))}))

(def app (-> page-counter
             wrap-session))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))

;; Additional tasks
;; 
;; 