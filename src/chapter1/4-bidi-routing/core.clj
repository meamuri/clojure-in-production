(ns core
  (:require [bidi.bidi :as bidi]))

(def routes
  ["/" {"" :page-index
        "hello" :page-hello
        true :not-found}])

(defn wrap-handler [handler]
  (fn [request]
    (let [{:keys [uri]} request
          request* (bidi/match-route* routes uri request)]
      (handler request*))))

(def wrapped (wrap-handler identity))

(comment
  (wrapped {:request-method :get :uri "/hello?foo=42"}))
