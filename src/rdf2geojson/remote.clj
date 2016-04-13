(ns rdf2geojson.remote
  (:require [clj-http.client :as client]
            [cemerick.url :refer (url url-encode)]
            [cheshire.core :refer :all]))
 
(def sample-query "PREFIX yago: <http://dbpedia.org/class/yago/> 
PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>
SELECT DISTINCT ?c ?lat ?long
WHERE {
?c a yago:CapitalsInEurope. ?c geo:lat ?lat . ?c geo:long ?long}
ORDER BY ?c")

(defn launch-query [query]
  (let [url (-> (url "http://dbpedia.org")
              (assoc :path "/sparql" :query (str "query=" (url-encode query)))
              str)]
    (client/get url {:accept :json})))

(defn get-results
  ([] (get-results sample-query))
  ([query] (:results (parse-string (:body (launch-query query)) true))))