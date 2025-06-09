(ns discogs.database
  "Database methods"
  (:require [discogs.client :as dc]
            [discogs.specs :as ds]
            [discogs.utils :refer [map-results]]
            [malli.core :as m]))

;; __________       .__
;; \______   \ ____ |  |   ____ _____    ______ ____
;;  |       _// __ \|  | _/ __ \\__  \  /  ___// __ \
;;  |    |   \  ___/|  |_\  ___/ / __ \_\___ \\  ___/
;;  |____|_  /\___  >____/\___  >____  /____  >\___  >
;;         \/     \/          \/     \/     \/     \/

(defn get-release
  "The Release resource represents a particular physical or
   digital object released by one or more Artists.

  Get a release by the `id`"
  {:added "0.1.0"}
  ([client release-id]
   (get-release client release-id nil))
  ([client release-id currency]
   (let [query (cond-> {}
                 currency (assoc :curr_abbr currency))]
     (if (m/validate ds/DiscogsReleasesParameters query)
       (->> (dc/mk-request client :get (format "/releases/%s" release-id) query map-results)
            first)
       (throw (ex-info "Input Validation Error" (m/explain ds/DiscogsReleasesParameters query)))))))

(defn get-release-rating-by-username
  "the release rating endpoint retrieves, updates, or deletes
   the rating of a release for a given user.

   retrieves the release’s rating for a given user.

   return:
    {:username \"memory\", :release_id 33849456, :rating 0}"
  {:added "0.1.0"}
  ([client release-id username]
   (->> (dc/mk-request client :get (format "/releases/%s/rating/%s" release-id username) {} map-results)
        first)))

(defn get-release-community-rating
  "the community release rating endpoint retrieves the average rating
   and the total number of user ratings for a given release.

  retrieves the community release rating average and count.

  return:
   {:release_id 33849456, :rating {:count 633, :average 4.84}}"
  {:added "0.1.0"}
  ([client release-id]
   (->> (dc/mk-request client :get (format "/releases/%s/rating" release-id) {} map-results)
        first)))

(defn get-release-stats
  "the release stats endpoint retrieves the total number of “haves” (in the community’s collections) and
   “wants” (in the community’s wantlists) for a given release.

  retrieves the release’s “have” and “want” counts.

  return:
   {:is_offensive false}"
  {:added "0.1.0"}
  ([client release-id]
   (->> (dc/mk-request client :get (format "/releases/%s/stats" release-id) {} map-results)
        first)))

;;    _____                   __
;;   /     \ _____    _______/  |_  ___________
;;  /  \ /  \\__  \  /  ___/\   __\/ __ \_  __ \
;; /    Y    \/ __ \_\___ \  |  | \  ___/|  | \/
;; \____|__  (____  /____  > |__|  \___  >__|
;;         \/     \/     \/            \/
;;

(defn get-master
  "The Master resource represents a set of similar Releases.
   Masters (also known as “master releases”) have a “main release” which is often the chronologically earliest.

   Get a master release."
  {:added "0.1.0"}
  ([client master-id]
   (->> (dc/mk-request client :get (format "/masters/%s" master-id) {} map-results)
        first)))


(defn get-master-versions
  "Retrieves a list of all Releases that are versions of this master."
  {:added "0.1.0"}
  ([client master-id]
   (get-master-versions client master-id {:sort "released"
                                          :sort_order "asc"
                                          :per_page 200}))
  ([client master-id params]
   (dc/mk-request client :get (format "/masters/%s/versions" master-id) params :versions)))

;;    _____          __  .__          __
;;   /  _  \________/  |_|__| _______/  |_
;;  /  /_\  \_  __ \   __\  |/  ___/\   __\
;; /    |    \  | \/|  | |  |\___ \  |  |
;; \____|__  /__|   |__| |__/____  > |__|
;;         \/                    \/

(defn get-artist
  "The Artist resource represents a person in the Discogs database
   who contributed to a Release in some capacity.

   Get an artist."
  {:added "0.1.0"}
  ([client artist-id]
   (->> (dc/mk-request client :get (format "/artists/%s" artist-id) {} map-results)
        first)))

(defn get-artist-releases
  "Returns a list of Releases and Masters associated with the Artist.

  Get an artist’s releases"
  {:added "0.1.0"}
  ([client artist-id]
   (get-artist-releases client artist-id {:sort "year"
                                          :sort_order "asc"}))
  ([client artist-id params]
   (dc/mk-request client :get (format "/artists/%s/releases" artist-id) params :releases)))

(defn get-artist-masters
  "Returns a list of Masters associated with the Artist.

  Get an artist’s master only"
  {:added "0.1.0"}
  ([client artist-id]
   (->> (get-artist-releases client artist-id)
        (filter (fn [{:keys [type]}] (= "master" type))))))

;; .____          ___.          .__
;; |    |   _____ \_ |__   ____ |  |
;; |    |   \__  \ | __ \_/ __ \|  |
;; |    |___ / __ \| \_\ \  ___/|  |__
;; |_______ (____  /___  /\___  >____/
;;         \/    \/    \/     \/

(defn get-label
  "The Label resource represents a label, company, recording studio, location, or other entity
   involved with Artists and Releases.

   Labels were recently expanded in scope to include things that aren’t labels –
   the name is an artifact of this history.

  Get a label."
  {:added "0.1.0"}
  ([client label-id]
   (->> (dc/mk-request client :get (format "/labels/%s" label-id) {} map-results)
        first)))

(defn get-label-releases
  "Returns a list of Releases associated with the label."
  {:added "0.1.0"}
  ([client label-id]
   (get-label-releases client label-id {:per_page 100}))
  ([client label-id params]
   (dc/mk-request client :get (format "/labels/%s/releases" label-id) params :releases)))

;;   _________                           .__
;;  /   _____/ ____ _____ _______   ____ |  |__
;;  \_____  \_/ __ \\__  \\_  __ \_/ ___\|  |  \
;;  /        \  ___/ / __ \|  | \/\  \___|   Y  \
;; /_______  /\___  >____  /__|    \___  >___|  /
;;         \/     \/     \/            \/     \/

(defn search
  "Sends a search request to the Discogs API using the given client and query parameters.

  The query parameters are validated against the DiscogsSearchParameters schema, which supports
  a wide range of search options for the Discogs database.

  **Parameters:**
  - `client`: The API client used to make the request.
  - `query`: A map of search parameters, matching the DiscogsSearchParameters schema.

  **Supported Query Parameters:**
  - `:q` (string, optional): General search query.
  - `:type` (DiscogsResourceType, optional): Filter by resource type (release, master, artist, or label).
  - `:title` (string, optional): Search by combined 'Artist Name - Release Title' field.
  - `:release_title` (string, optional): Search release titles.
  - `:credit` (string, optional): Search release credits.
  - `:artist` (string, optional): Search artist names.
  - `:anv` (string, optional): Search artist ANV (Artist Name Variation).
  - `:label` (string, optional): Search label names.
  - `:genre` (string, optional): Search genres.
  - `:style` (string, optional): Search styles.
  - `:country` (string, optional): Search release country.
  - `:year` (string, optional): Search release year.
  - `:format` (string, optional): Search formats.
  - `:catno` (string, optional): Search catalog number.
  - `:barcode` (string, optional): Search barcodes.
  - `:track` (string, optional): Search track titles.
  - `:submitter` (string, optional): Search submitter username.
  - `:contributor` (string, optional): Search contributor usernames.

  Returns:
  - On successful validation, returns the result of the API request.
  - If validation fails, throws an exception with explanation data.

  Example:
    (search client {:q \"Godflesh\" :type \"artist\"})

"
  {:added "0.1.0"}
  ([client query]
   (if (m/validate ds/DiscogsSearchParameters query)
     (dc/mk-request client :get "/database/search" query :results)
     (throw (ex-info "Input Validation Error" (m/explain ds/DiscogsSearchParameters query))))))
