(ns wrap-exception-middleware
  (:require [raven.client :as r]
            [infrastructure :refer [DSN]]
            [clojure.tools.logging :as log]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn wrap-exception
  [handler]
  (fn [request]
    (try 
      (handler request)
      (catch Exception e
        (let [event (-> nil
                        (r/add-exception! e)
                        (r/add-ring-request! request)
                        (r/add-extra! {:something "else"}))]
          (try
            @(r/capture! DSN event)
            (catch Exception e-sentry
              (log/errorf e-sentry "Sentry error: %s" DSN)
              (log/error e "Request failed")
              {:status 500
               :body {:msg "Internal error, please try later"}})
            (finally
              {:status 500
               :body {:msg "Internal error, please try later"}})))))))

(defn app* [request]
  (throw (ex-info "Alarm!" {:details (java.time.Instant/now)})))

(def app (-> app*
             wrap-exception
             wrap-json-response))

(def server (atom nil))

(defn start []
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))

(comment
  (start)
  (stop))
