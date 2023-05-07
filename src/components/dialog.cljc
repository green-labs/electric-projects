(ns components.dialog
  (:require [components.icon :refer [XMark]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]))


#?(:cljs
   (defn get-element-by-id [id]
     (.getElementById js/document id)))

#?(:cljs
   (defn show-success-modal []
     (-> (get-element-by-id "success-dialog")
         (.showModal))))

#?(:cljs
   (defn close-success-modal []
     (-> (get-element-by-id "success-dialog")
         (.close))))

#?(:cljs
   (defn register-close-handler [id]
     (let [elem (get-element-by-id id)]
       (.addEventListener elem "click" (fn [] (.close elem))))))

#?(:cljs
   (defn register-stop-propagation-handler [id]
     (let [elem (get-element-by-id id)]
       (.addEventListener elem "click" (fn [event] (.stopPropagation event))))))

(e/defn SuccessDialog []
  (dom/dialog
   (dom/props {:id    "success-dialog"
               :class "w-full bg-transparent p-0"})
   (dom/div
    (dom/props {:id    "success-dialog-inner"
                :class "p-8 w-full rounded-xl bg-white"})
    (dom/div
     (dom/props {:class "static flex flex-col gap-4"})
     (ui/button
      (e/fn [] #?(:cljs (close-success-modal)))
      (XMark.)
      (dom/props {:class "absolute top-0 right-0 m-6 hover:cursor-pointer"}))

     (dom/h2
      (dom/props {:class "text-green-600 w-full text-2xl font-bold text-center"})
      (dom/text "출석 완료"))
     (dom/div
      (dom/p
       (dom/props {:class "w-full text-xl text-center"})
       (dom/text "출석이"))
      (dom/p
       (dom/props {:class "w-full text-xl text-center"})
       (dom/text "완료되었습니다.")))
     (ui/button (e/fn [] #?(:cljs (close-success-modal)))
                (dom/text "확인")
                (dom/props {:class "bg-green-600 text-white p-4 rounded rounded-xl"})))))

  #?(:cljs (register-close-handler "success-dialog"))
  #?(:cljs (register-stop-propagation-handler "success-dialog-inner")))