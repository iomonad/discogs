(ns user
  (:require [discogs.database :as db]
            [discogs.client :as dc]))


(comment

  (def client (dc/mk-client "etohthu"))

  (db/search client {:q "Foo"})

)
