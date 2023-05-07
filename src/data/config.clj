(ns data.config
  (:require [aero.core :refer [read-config]]
            [clojure.java.io :as io]))

(def config (read-config (io/resource "config.edn")))
(defonce db-config (:db config))
(defonce auth-config (:auth config))