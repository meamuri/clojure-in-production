(ns store)

(def users (atom {:1 {:name "woofer"
                      :email "woofer@gmail.com"}
                  :2 {:name "meower"
                      :email "meower@gmail.com"}}))
