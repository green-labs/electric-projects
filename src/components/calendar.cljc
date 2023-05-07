(ns components.calendar
  (:require #?(:cljs ["headless-calendar" :refer [CalendarOfMonth]])
            [clojure.string :as string]
            [dom-util :refer [cs]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

#_{:clj-kondo/ignore [:unused-private-var]}
(def ^:private weeks [{:day-of-week "일"
                       :class "text-slate-400"}
                      {:day-of-week "월"
                       :class "text-slate-400"}
                      {:day-of-week "화"
                       :class "text-slate-400"}
                      {:day-of-week "수"
                       :class "text-slate-400"}
                      {:day-of-week "목"
                       :class "text-slate-400"}
                      {:day-of-week "금"
                       :class "text-slate-400"}
                      {:day-of-week "토"
                       :class "text-slate-400"}])

(e/defn DayOfWeek [{:keys [day-of-week class]}]
  (dom/div
   (dom/props
    {:class (cs "flex" "flex-col" "justify-center" "itmes-center"
                "h-18" "text-center" "rounded-md"
                class)})
   (dom/text day-of-week)))

(e/defn Day [{:keys [day-number
                     mark-present?
                     not-curr-month?
                     today?]}]
  (dom/div
   (dom/props {:class "flex flex-col items-center h-14"})
   (dom/div
    (dom/props {:class (string/join " " ["flex"
                                         "flex-col"
                                         "justify-center"
                                         "items-center"
                                         "h-9"
                                         "w-8 sm:w-12 md:w-16 lg:w-24"
                                         "text-right"
                                         "rounded-md"
                                         (when not-curr-month? "font-light text-slate-400")
                                         (when today? "border-2 border-green-600 shadow-inner")
                                         (when mark-present? "text-white bg-green-600 shadow-inner")])})
    (dom/text day-number))
   (when today? (dom/div
                 (dom/props {:class "text-xs text-center text-green-600"})
                 (dom/text "오늘")))))

#_{:clj-kondo/ignore [:unused-binding]}
(defn today? [month date]
  #?(:cljs
     (let [curr (js/Date.)
           curr-month (inc (.getMonth curr))
           curr-date (.getDate curr)]
       (and (= month curr-month) (= date curr-date)))))

(defn left-pad [x]
  (if (< 0 x 10) (str "0" x) x))

(defn calendar-day->date [date]
  (let [date (. date -date)]
    {:year (. date -year)
     :month (. date -month)
     :date (. date -date)}))

(defn format-of-plain-date [{:keys [year month date]}]
  (str year "-" (left-pad month) "-" (left-pad date)))

#_{:clj-kondo/ignore [:unused-binding]}
(e/defn Calendar [attendances]
  (let [attended-days (set (map :attended-at attendances))]
    (dom/div
     (dom/props {:class "flex justify-center pt-8 p-1 bg-slate-100 shadow-lg rounded-lg mt-4"})
     #?(:cljs
        (let [now (js/Date.)
              year (.getUTCFullYear now)
              prev-month (.getUTCMonth now)
              curr-month (inc prev-month)
              prev-calendar-of-month (CalendarOfMonth. year prev-month)
              calendar-of-month (CalendarOfMonth. year curr-month)
              start-date (first calendar-of-month)
              start-padding (. start-date -dayAxisIndex)
              prev-last-date (inc (. (last prev-calendar-of-month) -dayNumber))
              last-date (.. calendar-of-month -endDate -date)
              end-padding (- 7 (rem (+ start-padding last-date) 7))]
          (dom/div (dom/props {:class "grid w-full gap-x-2 grid-cols-7 grid-rows-7"})
                   (e/for [week weeks]
                     (DayOfWeek. week))
                   (e/for [day-number (range (- prev-last-date start-padding) prev-last-date)]
                     (let [yyyy-mm-dd (format-of-plain-date {:year year
                                                             :month prev-month
                                                             :date day-number})]
                       (Day. {:day-number day-number
                              :mark-present? (contains? attended-days yyyy-mm-dd)
                              :not-curr-month? true})))
                   (e/for [day calendar-of-month]
                     (let [day-number (. day -dayNumber)
                           yyyy-mm-dd (format-of-plain-date (calendar-day->date day))]
                       (Day. {:day-number day-number
                              :mark-present? (contains? attended-days yyyy-mm-dd)
                              :today? (today? curr-month day-number)})))
                   (e/for [day-number (range 0 end-padding)]
                     (Day. {:day-number (inc day-number)
                            :not-curr-month? true}))))))))

(e/defn AttencanceCalendar [attendances consecutive-days]
  (dom/div
   (dom/div
    (dom/props {:class "text-3xl mt-5"})
    (dom/text (str #?(:cljs (inc (.getMonth (js/Date.)))) "월")))
   (dom/div
    (dom/props {:class "flex justify-between"})
    (dom/span
     (dom/props {:class "text-xl flex gap-2"})
     (dom/span
      (dom/text "연속출석"))
     (dom/span
      (dom/props {:class "text-green-600"})
      (dom/text (str consecutive-days "일차"))))
    (dom/span
     (dom/props {:class "flex gap-2"})
     (dom/span
      (dom/props {:class "flex items-center gap-1"})
      (dom/div (dom/props {:class "w-4 h-4 rounded border-2 border-green-600"}))
      (dom/text "오늘"))
     (dom/span
      (dom/props {:class "flex items-center gap-1"})
      (dom/div (dom/props {:class "w-4 h-4 rounded bg-green-600"}))
      (dom/text "출석"))))
   (Calendar. attendances)))
