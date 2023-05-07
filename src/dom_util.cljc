(ns dom-util
  (:require [clojure.string :as string]))

(defn cs [& classes]
  (string/join " " classes))