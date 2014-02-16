(ns endafarrell.orla.handler
  (:use compojure.core
        ring.adapter.jetty
        ring.util.response
        [ring.middleware.format :only [wrap-restful-format]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [endafarrell.orla.core :as c]))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defroutes old-routes
  (GET "/" [] "hi!")
  (GET "/user/:id" [id] (c/read-data "user" id))
  (route/resources "/user/:id")
;  (POST "/user/new" [name]
;       (let [result (c/logging-save (feed.core.Participant. 0 name) c/save-participant-to-cache)]
;         (c/prepare-response-for-client result)))
;  (route/resources "/user/new")
;  (GET "/user/:id/friends" [id]
;       (let [result (c/logging-load (parse-int id) "class feed.core.Friend" c/load-friends-from-cache c/save-friend-to-cache c/load-friends-from-db)]
;         (c/prepare-response-for-client result)))
;  (route/resources "/user/:id/friends")
;  (POST "/user/:id/friends/new" [from to]
;       (let [result (c/logging-save (feed.core.Friend. 0 (parse-int from) (parse-int to)) c/save-friend-to-cache)]
;         (c/prepare-response-for-client result)))
;  (route/resources "/user/:id/friends/new")

        
  (route/not-found {:status 404 :body "not found"}))

(defroutes api-routes
  (GET "/" [] "hi!")
  (context "/api" []
    (OPTIONS "/" []
      (->
        (response {:version "0.2.0-SNAPSHOT"})
        (header "Allow" "OPTIONS")))
    (GET "/user/:id" [id] (response (c/read-data "user" id)))
    (ANY "/" []
      (->
        (response nil)
        (status 405)
        (header "Allow" "OPTIONS"))))
  (route/not-found "Nothing to see here, move along now"))

(def app
  (-> (handler/api api-routes)
      (wrap-restful-format)))

(defn -main []
  (run-jetty app {:port 3000}))