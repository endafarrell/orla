(ns endafarrell.orla.db.sqlite)

(def load-user-from-db-command "select id, data from users where id = ?;")
