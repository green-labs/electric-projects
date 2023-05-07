(ns app.attendance
  (:require [components.calendar :refer [AttencanceCalendar]]
            [components.content :refer [AttendanceButton CalendarImage
                                        Cheering Description Disclaimer Title]]
            [components.dialog :refer [#?(:cljs show-success-modal) SuccessDialog]]
            [data.db :as db]
            [data.token :refer [authorized] :as token]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom])
  #?(:cljs (:require-macros [data.token :refer [authorized]])))


(defn attendance-info-of-user [access-token]
   (let [user (token/get-user-from-access-token access-token)
         user-id (:id user)
         attendances (db/find-attendances-by-user-id-and-before-attended-at user-id 40)
         today (str #?(:clj (java.time.LocalDate/now)))
         attended-at's (set (map :attended-at attendances))]
     (if user
       {::user user     
        ::attended? (contains? attended-at's today)
        ::consecutive-days (:count (db/consecutive-days user-id))
        ::attendances attendances}
       {:error "Unauthorized"})))

(e/def !state (atom (let [initial-state (authorized
                                         (e/server
                                          (let [access-token (e/client
                                                              (token/local-storage-get-item
                                                               token/access-token-key))]
                                            (attendance-info-of-user access-token))))]
                      (prn :initial-state initial-state)
                      initial-state)))

(e/defn on-attendance []
  (e/server
   (let [state   (e/watch !state)
         user    (::user state)
         user-id (:id user)]
     (if (authorized (db/mark-as-attendance user-id))
       (let [access-token (e/client
                           (token/local-storage-get-item
                            token/access-token-key))
             attendance-info (attendance-info-of-user access-token)]
         (e/client
          (prn :attendance-info attendance-info)
          (swap! !state (constantly attendance-info))
          #?(:cljs (show-success-modal))))
       (e/client #?(:cljs (js/eval "alert('오늘은 이미 출석하셨습니다.')")))))))

(e/defn Attendence []
  (let [state (e/client (e/watch !state))
        user (::user state)
        error (:error state)
        attended? (::attended? state)
        consecutive-days (get state ::consecutive-days 0)
        attendances (get state ::attendances [])]
    (e/client
     (dom/div
      (dom/props {:class "container m-auto px-5 py-10"})
      (SuccessDialog.)

      (dom/div (dom/text (str "안녕하세요! " (or (:real_name user) "Unauthorized") " 님")))
      (dom/br)

      (Title.)
      (Description.)
      (CalendarImage.)
      (AttendanceButton. on-attendance attended? error)
      (Cheering. attended? error)
      (AttencanceCalendar. attendances
                           consecutive-days))

      ;; footer
     (Disclaimer.))))
