(ns discogs.algos
  "Some algorithms made using Discogs data"
  (:require [discogs.database :refer [search]]))

(defn search-artists->ids
  "Search for artists name, and return matching as id"
  ([client artist-name]
   (let [results (search client {:artist artist-name})]
     (into #{} (map :id results)))))
