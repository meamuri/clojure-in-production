(ns gears.html-gen
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn render [internal]
   (html5
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css "/style.css")]
    [:body
     [:div {:id "app"}]
     internal]))
