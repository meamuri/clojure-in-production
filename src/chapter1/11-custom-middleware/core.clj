(ns core
  (:require [clojure.walk :refer [keywordize-keys stringify-keys]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn wrap-headers-kw [handler]
  (fn [request]
    (-> request
        (update :headers keywordize-keys)
        handler
        (update :headers stringify-keys))))

(defn app* [request]
  (let [{:keys [headers]} request
        {:keys [host]} headers]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (format "<header></header><body>Actual host is %s</body>" host)}))

(def app (-> app*
             wrap-headers-kw))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment  
  (start)
  (stop))
