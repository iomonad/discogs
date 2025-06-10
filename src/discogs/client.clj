(ns discogs.client
  (:require [clj-http.client :as http]
            [jsonista.core :as json]
            [discogs.reporter :as report]
            [clojure.tools.logging :as log]))

(defn- client-version [] (System/getProperty "discogs.version"))
(defonce ^:private user-agent (format "clj-discogs/%s +https://github.com/iomonad/discogs" (client-version)))
(defonce ^:private api-endpoint "https://api.discogs.com")

(defn- ->env-token [] (System/getenv "DISCOGS_TOKEN"))

(defn mk-client
  "Build a Discogs API client.

  Usage:

  ;; Create Anonymous client, some API Methods will not work
  (def client (mk-client))

  ;; If you have `DISCOGS_TOKEN` env set
  (def client (mk-client)) ;; {:method :env-variable ...}

  ;; Create token with Key + Secret
  (def client (mk-client \"fooKm..9QeN\" \"wBaR..e8t0\"))

  ;; Create client with PAT
  (def client (mk-client \"cWyNjw....MfhcYOZ\"))"
  {:added "0.1.0"}
  ([]
   (merge
    (if-let [token (->env-token)]
      {:method :env-variable
       :token (atom token)}
      {:method :anonymous
       :token (atom nil)})
    {:quota-reporter (atom nil)}))
  ([api-key api-secret]
   (merge
    (mk-client)
    {:method :key+secret
     :token (atom
             (format "Discogs key=%s, secret=%s"
                     api-key api-secret))}))
  ([discogs-pat-token]
   (merge
    (mk-client)
    {:method :pat
     :token (atom (format "Discogs token=%s" discogs-pat-token))})))

(defn- headers->request-metrics
  "Extract discogs API metrics to a map

   Intended to be used with the reporter callback."
  [headers*]
  {:ratelimit {:used (read-string (get headers* "x-discogs-ratelimit-used"))
               :limit (read-string (get headers* "x-discogs-ratelimit"))
               :remaining (read-string (get headers* "x-discogs-ratelimit-remaining"))}
   :media-type (get headers* "x-discogs-media-type")})

(defn- pagination->next
  "Extract pagination target from body response"
  [{:keys [pagination]}]
  (get-in pagination [:urls :next]))

(defn- parse-results
  "Parse results as json using jsonista library"
  [body]
  (-> body
      (json/read-value json/keyword-keys-object-mapper)))

(defn- http-request
  [{:keys [url query-params] :as spec}]
  (log/debugf "client requesting discogs endpoint %s with params %s"
              url query-params)
  (http/request spec))

(defn mk-request
  "Make a request to the service - intended to be used for
   library internal only.

   Support native scrolling. disclaimer: the library don't let
   you manage the scrolling, every request will be fully realized.

   This can obviously leads to api limit responses."
  {:added "0.1.0"}
  ([client method resource parameters]
   (mk-request client method resource parameters identity))
  ([{:keys [token] :as client} method resource parameters extractor]
   (let [request*
         (cond-> {:method method
                  :headers (cond-> {:content-type "application/json"
                                    :accept "application/json"
                                    :user-agent user-agent}
                             @token (assoc :authorization @token))
                  :url (str api-endpoint resource)}
           parameters (assoc :query-params parameters))]
     (loop [results [] next nil]
       (let [request (if next
                       (merge (dissoc request* :url :query-params) {:url next})
                       request*)
             {:keys [headers body]} (http-request request)
             metrics-headers (headers->request-metrics headers)
             result (parse-results body)
             xtracted (extractor result)]
         (report/compute-reporter! client metrics-headers)
         (if-let [next-source (pagination->next result)]
           (recur (concat results xtracted) next-source)
           (concat results xtracted)))))))
