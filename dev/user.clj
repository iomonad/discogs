(ns user
  (:require [discogs.database :as db]
            [discogs.client :as dc]))


(comment

  (def client (dc/mk-client))

  (dc/set-quota-reporter-callback! client
                                   (fn [report]
                                     (println report)))

  (def results (db/search client {:label "Rephlex"}))
  )
