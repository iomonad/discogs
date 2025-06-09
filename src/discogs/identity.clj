(ns discogs.identity
  (:require [discogs.client :as dc]
            [discogs.utils :refer [map-results]]))

(defn whoami
  "Retrieve basic information about the authenticated user.

   You can use this resource to find out who you’re authenticated as, and it also
   doubles as a good sanity check to ensure that you’re using OAuth correctly.

   For more detailed information, make another request for the user’s Profile."
  {:added "0.1.0"}
  ([client]
   (first (dc/mk-request client :get "/oauth/identity" {} map-results))))

(defn get-profile
  "Retrieve a user by username.

  If authenticated as the requested user, the email key will be visible,
  and the num_list count will include the user’s private lists.

  If authenticated as the requested user or the user’s collection/wantlist is public,
  the num_collection / num_wantlist keys will be visible."
  {:added "0.1.0"}
  ([client username]
   (first (dc/mk-request client :get (format "/users/%s" username) {} map-results))))


(defn get-submissions
  "The Submissions resource represents all edits that a user makes
   to releases, labels, and artist."
  {:added "0.1.0"}
  ([client username]
   (->> (dc/mk-request client :get (format "/users/%s/submissions" username) {} map-results)
        first)))

(defn get-contributions
  "The Contributions resource represents releases, labels,
    and artists submitted by a user."
  {:added "0.1.0"}
  ([client username]
   (get-contributions client username {:sort "year"
                                       :sort_order "asc"}))
  ([client username params]
   (dc/mk-request client :get (format "/users/%s/contributions" username) params :contributions)))
