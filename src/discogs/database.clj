(ns discogs.database
  (:require [discogs.client :as dc]
            [discogs.specs :as ds]
            [malli.core :as m]))

(defn search
  ""
  {:added "0.1.0"}
  ([client query]
   (if (m/validate ds/DiscogsSearchParameters query)
     (dc/mk-request client :get "/database/search" query)
     (throw (ex-data (m/explain ds/DiscogsSearchParameters query))))))
