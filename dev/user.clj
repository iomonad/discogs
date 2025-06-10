(ns user
  (:require [discogs.client :as dc]
            [unilog.config :as ul]
            [discogs.reporter :refer [set-quota-reporter-callback!
                                      default-reporter]]))

(ul/start-logging! ul/default-configuration)

(comment

  (def client (dc/mk-client))

  (set-quota-reporter-callback! client default-reporter))
