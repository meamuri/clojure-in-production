(ns core
  (:require [sentry-clj.core :as sentry]
            [infrastructure :refer [DSN] :as infr]
            [raven.client :as raven]))

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
