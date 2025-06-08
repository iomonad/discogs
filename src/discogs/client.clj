(ns discogs.client
  (:require [clj-http.client :as http]
            [jsonista.core :as json]))

(def client-version (System/getProperty "discogs.version"))
(def user-agent (format "clj-discogs/%s +https://github.com/iomonad/discogs" client-version))

(def api-endpoint "https://api.discogs.com")

(defn mk-client
  "Build a client"
  ([discogs-pat-token]
   {:method :pat
    :token (atom discogs-pat-token)})
  ([discogs-key discogs-secret]
   {:method :key+secret
    ;; Not Implemented for the
    ;; Moment
    :token (atom nil)}))

(defn- headers->request-metrics
  "Extract Discogs API metrics to a Map"
  [headers*]
  {:ratelimit {:used (read-string (get headers* "x-discogs-ratelimit-used"))
               :limit (read-string (get headers* "x-discogs-ratelimit"))
               :remaining (read-string (get headers* "x-discogs-ratelimit-remaining"))}
   :media-type (get headers* "x-discogs-media-type")})

(defn mk-request
  ([{:keys [token] :as client} method resource parameters]
   (let [request*
         (cond-> {:method method
                  :debug true
                  :headers {:content-type "application/json"
                            :accept "application/json"
                            :user-agent user-agent
                            :authorization (format "Discogs token=%s" @token)}
                  :url (str api-endpoint resource)}
           parameters (assoc :query-params parameters))]
     (try
       (let [{:keys [status headers body]} (http/request request*)
             metrics-headers (headers->request-metrics headers)
             success? (< status 400)]
         {:result (-> body (json/read-value json/keyword-keys-object-mapper))
          :success success?
          :metrics metrics-headers})
       (catch Exception e
         {:success false
          :failure (.getMessage e)})))))
