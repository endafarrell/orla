(ns endafarrell.orla.core
  (:require [endafarrell.orla.db.db :as db]
            [clojure.java.jdbc :as j]))


(defn load-user-from-db
  [id]
  (println "starting load-user-from-db")
  (try
    (j/with-connection db/db-spec
      (println "in with-connection")
      (let [rows (j/query db/db-spec [db/load-user-from-db-command id])
            user {:type "user"
                  :id (:id (first rows))
                  :data (:data (first rows))}]
        (println (format "there are %d results, firs is %s" (count rows) user))
        user))
    (catch Exception e
      (println "\nOh dear.\n")
      (.printStackTrace e)
      (j/print-sql-exception-chain e))))

(defn save-user-to-db
  [user]
  (j/with-connection db/db-spec
    (j/insert! db/db-spec "users"
                          ["id" "data"]
                          [(:id user) (:data user)])))

(defn read-data [entity-type id]
  (cond
    (= entity-type "user") (load-user-from-db id)
    :else (throw (IllegalArgumentException. (str "type `" entity-type "` is not implemented.")))))
