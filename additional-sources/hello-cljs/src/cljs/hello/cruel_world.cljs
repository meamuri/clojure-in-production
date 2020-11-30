(ns hello.cruel-world
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom])) 

(defn simple-component []
  [:div
   "Extremley simple component"])

(defn mount-root []
  (rdom/render [simple-component] (.getElementById js/document "app")))

(defn init! []  
  (mount-root))

(js/console.log "Hello there world!")
(init!)
