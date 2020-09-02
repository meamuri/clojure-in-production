(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cookies :refer [wrap-cookies]]))

(defn page-seen [request]
  (let [{:keys [cookies]} request
        seen-path ["seen" :value]
        seen? (get-in cookies seen-path)
        cookies* (assoc-in cookies seen-path true)]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :cookies cookies*
     :body (if seen?
             "Hello again!"
             "Welcome to product web-page")}))

(def app (-> page-seen
             wrap-cookies))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
