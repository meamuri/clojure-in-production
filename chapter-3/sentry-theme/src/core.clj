(ns sentry-theme
  (:require [sentry-clj.core :as sentry]
            [config.core :refer [env]]
            [raven.client :as raven]))

(def DSN (:sentry-dns env))

(sentry/init! DSN)

(def constant-e
  (ex-info
   "Get user info error"
   {:user-id 42}
   (ex-info "Auth error"
            {:token "...."}
            (ex-info "HTTP error"
                     {:method "POST"
                      :url "https://api.site.com"}))))

(comment 
  (sentry/send-event {:throwable constant-e})
  (raven/capture! DSN constant-e))
