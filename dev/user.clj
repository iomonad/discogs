(ns user
  (:require [discogs.client :as dc]
            [discogs.database :as dd]
            [unilog.config :as ul]
            [discogs.reporter :refer [set-quota-reporter-callback!
                                      default-reporter]]))

(ul/start-logging!
 (assoc ul/default-configuration :overrides {"discogs" :debug}))

(comment

  (def client (dc/mk-client))

  (set-quota-reporter-callback! client default-reporter)

  (def master (dd/get-master client 13153))

  (dd/search client {:artist "Godflesh"}))
