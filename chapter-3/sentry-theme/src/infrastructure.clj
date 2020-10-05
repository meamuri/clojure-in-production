(ns infrastructure
  (:require [sentry-clj.core :as sentry]
            [config.core :refer [env]]))

(def DSN (:sentry-dsn env))

(sentry/init! DSN)
