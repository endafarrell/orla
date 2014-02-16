(ns endafarrell.orla.core-test
  (:require [clojure.test :refer :all]
            [endafarrell.orla.core :as c]))

(deftest load-user-from-db-test
  (testing "Cannot read user from database"
    (is (= "enda.farrell@gmail.com" (:id (c/load-user-from-db "enda.farrell@gmail.com")))))
  (testing "Cannot read by type"
    (is (= "enda.farrell@gmail.com" (:id (c/read-data "user" "enda.farrell@gmail.com"))))))
