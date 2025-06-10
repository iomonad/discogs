(ns discogs.marketplace
  "Marketplace methods"
  (:require [discogs.client :as dc]
            [discogs.utils :refer [map-results]]))

(defn get-user-inventory
  "Returns the list of listings in a user’s inventory.

   Basic information about each listing and the corresponding release is provided,
   suitable for display in a list. For detailed information about the release,
   make another API call to fetch the corresponding Release.

   If you are not authenticated as the inventory owner, only items that have a status of For Sale will be visible.
   If you are authenticated as the inventory owner you will get additional weight, format_quantity, external_id,
   location, and quantity keys.

   Note that quantity is a read-only field for NearMint users, who will see 1 for all quantity values, regardless of their actual count.
   If the user is authorized, the listing will contain a in_cart boolean field
   indicating whether or not this listing is in their cart."
  {:added "0.1.1"}
  ([client username]
   (get-user-inventory client username {:sort "price"
                                        :sort_order "desc"}))
  ([client username parameters]
   (dc/mk-request client :get (format "/users/%s/inventory" username) parameters :listings)))

(defn get-listing
  "View the data associated with a listing.
  If the authorized user is the listing owner the listing will include the weight,
  format_quantity, external_id, location, and quantity keys.

  Note that quantity is a read-only field for NearMint users, who will see 1 for
  all quantity values, regardless of their actual count.

  If the user is authorized, the listing will contain a in_cart
  boolean field indicating whether or not this listing is in their cart."
  {:added "0.1.1"}
  ([client listing-id]
   (get-listing client listing-id {}))
  ([client listing-id params]
   (->> (dc/mk-request client :get (format "/marketplace/listings/%s" listing-id) params map-results)
        first)))

(defn calculate-fee
  "The Fee resource allows you to quickly calculate the fee
   for selling an item on the Marketplace."
  {:added "0.1.1"}
  ([client ^Long price]
   (calculate-fee client price {:currency "USD"}))
  ([client ^Long price params]
   (->> (dc/mk-request client :get (format "/marketplace/fee/%s" price) params map-results)
        first)))

(defn get-price-suggestions
  "Retrieve price suggestions for the provided Release ID.

  If no suggestions are available, an empty object will be returned.

  Authentication is required, **and the user needs to have filled out their seller settings**.
  Suggested prices will be denominated in the user’s selling currency."
  {:added "0.1.1"}
  ([client release-id]
   (->> (dc/mk-request client :get (format "/marketplace/price_suggestions/%s" release-id) {} map-results)
        first)))

(defn get-selling-statistics
  "Retrieve marketplace statistics for the provided Release ID.

  These statistics reflect the state of the release in the marketplace currently,
  and include the number of items currently for sale, lowest listed price of
  any item for sale, and whether the item is blocked for sale in the marketplace.

  Authentication is optional. Authenticated users will by default have the lowest
  currency expressed in their own buyer currency, configurable in buyer settings,
  in the absence of the curr_abbr query parameter to specify the currency.

  Unauthenticated users will have the price expressed in US Dollars, if no curr_abbr is provided.

  Releases that have no items for sale in the marketplace will return a body with
  null data in the lowest_price and num_for_sale keys.

  Releases that are blocked for sale will also have null data for these keys."
  {:added "0.1.1"}
  ([client release-id]
   (get-selling-statistics client release-id {:curr_abbr "USD"}))
  ([client release-id params]
   (->> (dc/mk-request client :get (format "/marketplace/stats/%s" release-id) params map-results)
        first)))
