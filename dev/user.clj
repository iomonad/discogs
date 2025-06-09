(ns user
  (:require [discogs.database :as db]
            [discogs.identity :as di]
            [discogs.client :as dc]
            [unilog.config :as ul]
            [discogs.reporter :refer [set-quota-reporter-callback!]]
            [clojure.tools.logging :as log]))


(ul/start-logging! ul/default-configuration)

(comment

  (def client (dc/mk-client))

  (set-quota-reporter-callback!
   client
   (fn [{:keys [ratelimit]}]
     (let [{:keys [remaining]} ratelimit]
       (when (> 10 remaining)
         (log/warnf "quota almost exhausted (%s)" ratelimit)))))

  (def results (db/search client {:label "Cavage"}))
  (def results (db/get-release client 33849456 "EUR"))

  (clojure.pprint/pprint (db/get-release-rating-by-username client 33849456 "memory"))
  (clojure.pprint/pprint (db/get-release-community-rating client 33849456))
  (clojure.pprint/pprint (db/get-release-stats client 81247))
  (clojure.pprint/pprint (db/get-master client 13142))
  (count (db/get-master-versions client 13142))

  (clojure.pprint/pprint (db/get-artist client 34047))
  (def all-releases )

  (count (db/get-artist-releases client 34047))
  (clojure.pprint/print-table (db/get-artist-masters client 34047))
  (count all-releases)
  (clojure.pprint/print-table all-releases)

  (db/get-label client 4649)
  (def x (db/get-label-releases client 4649))
  (count x)

  (clojure.pprint/pprint x)

  (di/identity client)
  (clojure.pprint/pprint (di/get-profile client "macron"))

  (clojure.pprint/print-table (di/get-submissions client "djgtek"))
  (clojure.pprint/pprint (di/get-contributions client "djgtek"))
  )
