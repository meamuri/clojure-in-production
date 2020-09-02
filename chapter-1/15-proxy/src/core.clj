(ns core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [clj-http.client :as client]))


(defn app [request]
  (-> "https://ya.ru"
      (client/get {:stream? true})
      (select-keys [:status :body :headers])
      (update :headers select-keys ["Content-Type"] )))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
