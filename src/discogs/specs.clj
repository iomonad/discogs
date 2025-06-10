(ns discogs.specs
  "Specs & Contracts for input and output validation"
  (:require [malli.core :as m]))

(def DiscogsResourceType
  [:enum "release" "master" "artist" "label"])

(def DiscogsReleasesParameters
  [:map
   {:title "Releases Parameters"
    :description "Parameters for Release route"
    :closed true}
   [:curr_abbr {:optional true} :string]])

(def DiscogsSearchParameters
  [:map
   {:title "Search Parameters"
    :description "Parameters for search API"
    :closed true}
   [:q {:optional true
        :description "Your search query"}
    :string]
   [:type {:optional true
           :description "One of release, master, artist, label"}
    DiscogsResourceType]
   [:title {:optional true
            :description "Search by combined 'Artist Name - Release Title' title field"}
    :string]
   [:release_title {:optional true
                    :description "Search release titles"}
    :string]
   [:credit {:optional true
             :description "Search release credits"}
    :string]
   [:artist {:optional true
             :description "Search artist names"}
    :string]
   [:anv {:optional true
          :description "Search artist ANV"}
    :string]
   [:label {:optional true
            :description "Search label names"}
    :string]
   [:genre {:optional true
            :description "Search genres"}
    :string]
   [:style {:optional true
            :description "Search styles"}
    :string]
   [:country {:optional true
              :description "Search release country"}
    :string]
   [:year {:optional true
           :description "Search release year"}
    :string]
   [:format {:optional true
             :description "Search formats"}
    :string]
   [:catno {:optional true
            :description "Search catalog number"}
    :string]
   [:barcode {:optional true
              :description "Search barcodes"}
    :string]
   [:track {:optional true
            :description "Search track titles"}
    :string]
   [:submitter {:optional true
                :description "Search submitter username"}
    :string]
   [:contributor {:optional true
                  :description "Search contributor usernames"}
    :string]])
