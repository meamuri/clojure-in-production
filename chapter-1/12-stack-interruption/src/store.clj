(ns store)


(def users-store (atom {:1 {:name "corjure" :email "corjure@corz.com" :password "woof!"}
                        :2 {:name "meowch" :email "meowch@meow.com" :password "meow"}}))

(defn retrieve-user-from-store [user-id]
  (get @users-store user-id))
