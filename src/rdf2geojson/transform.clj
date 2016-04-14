(ns rdf2geojson.transform
  (:require [cheshire.core :refer :all]))

(defn point [lat long] 
  {:type :Point :coordinates [lat, long]})

(defn line [coords] 
  {:type :LineString :coordinates (into [] coords)})

(defn polygon [coords] 
  {:pre [(= (first coords) (last coords))]} 
  {:type :Polygon :coordinates [(into [] coords)]})



(def countries (parse-string (slurp "resources/test.geo.json") true))
(defn get-country-poly [k, v]
 #_{:pre (or (= k "ADMINs") (= k "ISO_A3"))}
  (let [f (if (seq? v) contains? =)]
    (map #(print (k (:properties %))) v)
    f
    #_(filter w countries)))