(ns discogs.client
  (:require [clj-http.client :as http]
            [jsonista.core :as json]
            [discogs.reporter :as report]))

(defn- client-version [] (System/getProperty "discogs.version"))
(defonce user-agent (format "clj-discogs/%s +https://github.com/iomonad/discogs" (client-version)))
(defonce api-endpoint "https://api.discogs.com")

(defn mk-client
  "Build a Discogs API client.

  0. No argument => Anonymous
  1. Pat Token => Use Account
  2. Key & Secret => Registered Application"
  {:added "0.1.0"}
  ([]
   {:method :anonymous
    :token (atom nil)
    :quota-reporter (atom nil)})
  ([discogs-pat-token]
   (merge
    (mk-client)
    {:method :pat
     :token (atom discogs-pat-token)}))
  ([_discogs-key _discogs-secret]
   (merge
    (mk-client)
    {:method :key+secret
     ;; Not Implemented for the
     ;; Moment
     :token (atom nil)})))

(defn- headers->request-metrics
  "Extract discogs api metrics to a map"
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

(defn mk-request
  "Make a request to the service - intended to be used for
   library internal only.

   Support native scrolling. disclaimer: the library don't let
   you manage the scrolling, every request will be fully realized.

   This can obviously leads to api limit responses."
  {:added "0.1.0"}
  ([{:keys [token] :as client} method resource parameters]
   (let [request*
         (cond-> {:method method
                  :headers {:content-type "application/json"
                            :accept "application/json"
                            :user-agent user-agent
                            :authorization (format "Discogs token=%s" @token)}
                  :url (str api-endpoint resource)}
           parameters (assoc :query-params parameters))]
     (try
       (loop [results []
              next nil]
         (let [request (if next
                         (merge (dissoc request* :url :query-params) {:url next})
                         request*)
               {:keys [headers body]} (http/request request)
               metrics-headers (headers->request-metrics headers)
               result (parse-results body)]
           (report/compute-reporter! client metrics-headers)
           (if-let [next-source (pagination->next result)]
             (recur (concat results (:results result)) next-source)
             results)))
       (catch Exception e
         {:success false
          :failure (.getMessage e)})))))
