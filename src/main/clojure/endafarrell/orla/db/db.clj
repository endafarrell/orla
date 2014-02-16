(ns endafarrell.orla.db.db
  (:require [endafarrell.orla.db.sqlite :as s]))


(def  db-spec { :classname   "org.sqlite.JDBC"
               :subprotocol "sqlite"
               :subname     "db/sqlite3/orla.db"})

(def load-user-from-db-command s/load-user-from-db-command)