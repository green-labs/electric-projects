(ns components.content
  (:require [dom-util :refer [cs]]
             [components.icon :refer [QuestionMarkCircle]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]))

(e/defn Title []
  (dom/div (dom/props {:class "text-2xl md:text-4xl font-bold pb-2"})
           (dom/div (dom/text "매일매일 출석체크"))
           (dom/div (dom/text "출석왕에 도전해보세요!"))))

(e/defn Description []
  (dom/div
   (dom/props {:class "group flex relative"})
   (ui/button
    (e/fn [])
    (QuestionMarkCircle.)
    (dom/text "출석체크 안내")
    (dom/props {::class "flex flex-row gap-1 items-center text-sm h-8 text-slate-500"}))
   (dom/span (dom/text "계정당 1일 1회 한정으로 참여가능합니다.")
             (dom/props {:class "group-hover:opacity-100 transition-opacity bg-gray-800 pl-4 p-2 text-sm text-gray-100 rounded-md absolute left-1/2 
    -translate-x-1/2 translate-y-full opacity-0 m-4 mx-auto"}))))

(e/defn CalendarImage []
  (dom/div
   (dom/props {:class "my-10 w-1/2 mx-auto"})
   (dom/img (dom/props {:src "https://img.freepik.com/free-vector/calendar-icon-on-white-background_1308-84634.jpg?w=2000"}))))

(e/defn AttendanceButton [on-attendance
                          attended?
                          error]
  (let [disabled? (or (true? attended?) (nil? attended?))]
    (ui/button
     on-attendance (dom/text (cond
                               (true? attended?) "출석체크 완료"
                               (false? attended?) "오늘 출석체크하기"
                               :else error))
     (dom/props {:disabled disabled?
                 :class    (cs
                            "text-md lg:text-xl w-full rounded-lg px-8 py-4 font-semibold"
                            (if disabled? "text-slate-500 bg-gray-200" "text-white bg-green-600"))})))
  )

(e/defn Cheering [attended? error]
  (dom/div
   (dom/props {:class "text-gray-500 text-center mt-2"})
   (dom/text 
    (cond
      (true? attended?) "내일 또 만나요 ;)"
      (false? attended?) "출석왕까지 꾸준히 도전!"
      :else error))))

(e/defn Disclaimer []
  (dom/div
   (dom/props {:class "bg-gray-100 py-4 px-8 text-gray-500"})
   (dom/div
    (dom/props {:class "font-bold"})
    (dom/text "[유의사항]"))
   (dom/ul
    (dom/props {:class "list-disc list-inside"})
    (dom/li (dom/text "본 이벤트는 당사 사정에 따라 고지 없이 변경, 종료 될 수 있습니다."))
    (dom/li (dom/text "출석체크는 ID당 1일 1회 참여 가능합니다."))
    (dom/li (dom/text "모바일 기종 및 OS환경에 따라 화면이 잘 보이지 않을 수 있으며 최신 OS 업데이트가 필요합니다.")))))