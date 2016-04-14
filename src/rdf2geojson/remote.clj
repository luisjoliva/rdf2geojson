(ns rdf2geojson.remote
  (:require [clj-http.client :as client]
            [cemerick.url :refer (url url-encode)]
            [cheshire.core :refer :all]))

(def sample-query (slurp "resources/sample-sparql"))

(defn launch-query [query]
 (let [url (-> (url "http://dbpedia.org")
             (assoc :path "/sparql" :query (str "query=" (url-encode query)))
             str)]
   (client/get url {:accept :json})))

(defn get-results
  ([] (get-results sample-query))
  ([query] (:results (parse-string (:body (launch-query query)) true))))