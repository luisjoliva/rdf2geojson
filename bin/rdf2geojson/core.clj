(ns rdf2geojson.core
  (use edu.ucdenver.ccp.kr.kb
       edu.ucdenver.ccp.kr.rdf
       edu.ucdenver.ccp.kr.sparql)
  (require edu.ucdenver.ccp.kr.jena.kb))

(defn jena-memory-test-kb []
  (open (kb :jena-mem)))

(defn jena-remote-test-kb []
  (open
   (edu.ucdenver.ccp.kr.sesame.kb/new-sesame-server
    :server "http://dbpedia.org/sparql"
    :repo-name "")))


(defn add-namespaces [kb]
  (register-namespaces kb
                       '(("ex" "http://www.example.org/") 
                         ("rdf" "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
                         ("rdfs" "http://www.w3.org/2000/01/rdf-schema#")
                         ("owl" "http://www.w3.org/2002/07/owl#")
                         ("foaf" "http://xmlns.com/foaf/0.1/")
                         ("dbpedia-owl" "http://dbpedia.org/ontology/")
                         ("dbpedia" "http://dbpedia.org/resource/"))))

(def my-kb (add-namespaces (jena-memory-test-kb)))

(def ski-pattern '((?/ski rdf/type dbpedia-owl/SkiArea)))

(defn count-ski [kb]
  (query-count kb ski-pattern))

(defn ski-areas [kb]
  (query kb '((?/ski rdf/type dbpedia-owl/SkiArea))))

(defn visit-ski-areas [kb]
  (query-visit kb
               (fn [bindings]
                 (let [area (get bindings '?/ski)]
                   (println "visiting " (name area))))
               ski-pattern))


;;a couple of ways to add triples
(defn add-triples [kb]
  ;;in parts
  (add kb 'ex/KevinL 'rdf/type 'ex/Person)

  ;;as a triple
  (add kb '(ex/KevinL foaf/name "Kevin Livingston"))

  ;;to the 'default' kb
  (binding [*kb* kb]
    (add '(ex/KevinL foaf/mbox "<mailto:kevin@example.org>")))
  
  ;;multiple triples
  (add-statements kb
                  '((ex/BobL rdf/type ex/Person)
                    (ex/BobL foaf/name "Bob Livingston")
                    (ex/BobL foaf/mbox "<mailto:bob@example.org>"))))


;;; --------------------------------------------------------
;;; rdf api
;;; --------------------------------------------------------

;;rdf api ask
(defn ask-person [kb]
  (ask-rdf kb nil nil 'ex/Person))

;;rdf api query
(defn query-person [kb]
  (query-rdf kb nil nil 'ex/Person))


;;; --------------------------------------------------------
;;; sparql api
;;; --------------------------------------------------------

;; symbols in the ? ns are assumed to be capturing variables
;; symbols in the _ ns are blank nodes
;;    they function as non-capturing in sparql
(def names-and-email-pattern
  '((_/person rdf/type ex/Person)
    (_/person foaf/name ?/name)
    (_/person foaf/mbox ?/email)))

;;sparql api ask
(defn ask-names-with-email [kb]
  (ask kb names-and-email-pattern))

;;sparql api ask
(defn query-names-with-email [kb]
  (query kb names-and-email-pattern))

(defn visit-people [kb]
  (query-visit kb
               (fn [bindings]
                 (let [name (get bindings '?/name)
                       email (get bindings '?/email)]
                   (println "emailing " name " at: " email)))
               names-and-email-pattern))