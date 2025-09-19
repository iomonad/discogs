(ns discogs.algos
  "Some algorithms made using Discogs data"
  (:require [discogs.database :refer [search
                                      get-artist]]))

(defn search-artists->ids
  "Search for artists name, and return matching as id"
  {:added "0.1.3"}
  ([client artist-name]
   (let [results (search client {:artist artist-name})]
     (into #{} (map :id results)))))

(defn similar-artists
  "Search for similar artists

   Return a list of map with Artist name and id, NOT the
   full artist resource"
  {:added "0.1.4"}
  ([client artist-id]
   (similar-artists client artist-id {:depth 1}))
  ([client artist-id {:keys [depth]}]
   (let [{:keys [members aliases groups] :as artist} (get-artist client artist-id)]
     (cond->> []
       (seq aliases) (concat aliases)

       (seq groups)  (concat groups)

       (seq members)
       ((fn add-members [res]
          (when-let
           [mg (seq
                (mapcat
                 (fn [{:keys [id]}]
                   (let [{:keys [groups aliases]} (get-artist client id)]
                     (concat groups aliases)))
                 members))]
            (concat res mg))))
       :always (sort-by :name)
       :always (into #{})))))
