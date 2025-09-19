(ns user
  (:require [discogs.client :as dc]
            [discogs.database :as dd]
            [discogs.algos :as da]
            [unilog.config :as ul]
            [discogs.reporter :refer [set-quota-reporter-callback!
                                      default-reporter]]))

(ul/start-logging!
 (assoc ul/default-configuration :overrides {"discogs" :debug}))
