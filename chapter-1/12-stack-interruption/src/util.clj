(ns util
  (:require [clojure.walk :refer [keywordize-keys stringify-keys]]))

(defn wrap-headers-kw [handler]
  (fn [request]
    (-> request
        (update :headers keywordize-keys)
        handler
        (update :headers stringify-keys))))
