(ns pythagoras-tree.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   ["d3-scale-chromatic" :as d3-scale-chromatic]))

(def mouse-coords (r/atom [0 0]))
(.addEventListener js/document "mousemove"
                   #(reset! mouse-coords                           
                            [(- (.-pageX %) 425) (- 400 (.-pageY %))])
                   false)

(def interpolate-viridis (.-interpolateViridis d3-scale-chromatic))

(defn pythagoras [{:keys [mouse-pos size x y height-factor left right lvl max-lvl A B] :as opts}]
  (when (< lvl max-lvl)
    (let [[mouse-x mouse-y] mouse-pos
          next-step (* size height-factor)
          draw-size (max 0 (+ size (/ mouse-y 10)))
          translate (str "translate(" (+ x (/ mouse-x 10)) " " y ")")
          rotate (cond
                   left (str "rotate(" (- A) " 0 " size ")")
                   right (str "rotate(" B " " size " " size ")")
                   :else "")]
      [:g
       {:transform (str translate " " rotate)}
       [:rect
        {:width  draw-size
         :height draw-size
         :x      0
         :y      0
         :style  {:fill (interpolate-viridis (/ lvl max-lvl))}}]
       (pythagoras
        (assoc opts
               :x 0
               :y (- next-step)
               :size next-step
               :lvl (inc lvl)
               :left true
               :right false))
       (pythagoras
        (assoc opts
               :x (- size next-step)
               :y (- next-step)
               :size next-step
               :lvl (inc lvl)
               :left false
               :right true))])))

(defn svg []
  [:svg
   {:width "100%" :height 1000}
   (pythagoras
    {:mouse-pos @mouse-coords
     :x             425
     :y             200
     :size          50
     :height-factor 0.7
     :lvl           1
     :max-lvl       11
     :A             45
     :B             45})])

(defn home-page []
  [:div
   [:h2 "Pythagoras Tree"]
   [svg]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))

