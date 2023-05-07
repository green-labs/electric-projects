(ns components.icon
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-svg :as svg]))

(e/defn QuestionMarkCircle []
  (svg/svg
   (dom/props
    {:fill "none"
     :viewBox "0 0 24 24"
     :stroke-width "1.5"
     :stroke "currentColor"
     :class "w-6 h-6"})
   (svg/path
    (dom/props
     {:stroke-linecap "round"
      :stroke-linejoin "round"
      :d "M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z"}))))

(e/defn XMark []
  (svg/svg
   (dom/props {:fill "none"
               :viewBox "0 0 24 24"
               :stroke-width "1.5"
               :stroke "currentColor"
               :class "w-6 h-6"})
   (svg/path (dom/props {:stroke-line-cap "round"
                         :stroke-line-join "round"
                         :d "M6 18L18 6M6 6l12 12"}))))



