(ns data.db
  #?(:clj
     (:require [data.config :refer [db-config]]
               [honey.sql :as sql]
               [next.jdbc :as jdbc]
               [next.jdbc.result-set :as rs]
               [next.jdbc.sql :as jdbc.sql])))

#?(:clj (def ^:private datasource
          (jdbc/get-datasource db-config)))

#?(:clj (defn- execute! [ds query]
          (jdbc/execute!
           ds (sql/format query)
           {:builder-fn rs/as-unqualified-kebab-maps})))

#?(:clj (defn- process-row [attendance]
          (let [attended-at    (:attended-at attendance)
                attended-year  (.getYear attended-at)
                attended-month (.getMonth attended-at)
                attended-day   (.getDate attended-at)]
            (-> attendance
                (assoc :attended-year attended-year)
                (assoc :attended-month attended-month)
                (assoc :attended-day attended-day)
                (update :attended-at str)
                (update :created-at str)
                (update :updated-at str)))))

#_{:clj-kondo/ignore [:unused-binding]}
(defn find-attendances-by-user-id-and-before-attended-at [user-id days]
  #?(:clj (->> (execute! datasource {:select :*
                                     :from   :attendances
                                     :where  [:and
                                              [:= :user_id user-id]
                                              [:> :attended-at [:DATE_SUB [:NOW] [:interval days :DAY]]]]})
               (map process-row))))

#_{:clj-kondo/ignore [:unused-binding]}
(defn consecutive-days [user-id]
  #?(:clj (let [result (first
                        (execute!
                         datasource
                         {:with     [[:starting_points
                                      {:select [:*
                                                [[:case
                                                  [:<
                                                   [:DATE_ADD
                                                    [:over
                                                     [[:LAG :attended_at :1 :''1900-01-01']
                                                      {:order-by [:attended_at]}]]
                                                    [:interval :1 :DAY]]
                                                   :attended_at]
                                                  :1] :is_start]]
                                       :from   [:attendances]
                                       :where  [:= :user-id user-id]}]
                                     [:grouped
                                      {:select [:*
                                                [[:over
                                                  [[:count :is_start]
                                                   {:order-by [[:attended_at (keyword "ROWS UNBOUNDED PRECEDING")]]}]]
                                                 :group_id]]
                                       :from   [:starting_points]}]]
                          :select   [[[:MIN :attended_at] :start_date]
                                     [[:MAX :attended_at] :end_date]
                                     [[:count :*] :count]]
                          :from     :grouped
                          :group-by [:group-id]
                          :having   [:and
                                     [:> [:count :*] :0]
                                     [:<= [:- [:curdate] [:interval :1 :DAY]] :end_date]]
                          :order-by [[:group-id :DESC]]
                          :limit    :1}))]
            (if result
              (-> result
                  (update :start-date str)
                  (update :end-date str))
              {:count 0}))))

#_{:clj-kondo/ignore [:unused-binding]}
(defn- exists-attendance-today-by-user-id? [user-id]
  #?(:clj (-> (execute! datasource {:select :*
                                    :from   :attendances
                                    :where  [:and
                                             [:= :user-id user-id]
                                             [:= :attended-at [:CURRENT_DATE]]]})
              first
              some?)
     :default false))

#_{:clj-kondo/ignore [:unused-binding]}
(defn mark-as-attendance [user-id]
  #?(:clj
     (if-not (exists-attendance-today-by-user-id? user-id)
       (some-> (jdbc.sql/insert! datasource
                                 :attendances
                                 {:user_id user-id})
               :GENERATED_KEY
               zero?
               not)
       false)
     :default false))

(comment
  (consecutive-days 865237)
  (mark-as-attendance 865237)
  (exists-attendance-today-by-user-id? 865237)

  (exists-attendance-today-by-user-id? 865237))
