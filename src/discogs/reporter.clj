(ns discogs.reporter
  "Reporting callback about API Quota"
  (:require [clojure.tools.logging :as log]))

(defn set-quota-reporter-callback!
  "Attach a callback to the reporter. The function should be arity one.

  If not setup, the client will most likely echo nothing in the logging system.
   "
  {:added "0.1.0"}
  [client callback-fn]
  (update-in client [:quota-reporter]
             #(swap! % (constantly callback-fn))))

(def default-reporter
  (fn [{:keys [ratelimit]}]
    (let [{:keys [remaining]} ratelimit]
      (when (> 10 remaining)
        (log/warnf "quota almost exhausted (%s)" ratelimit)))))

(defn compute-reporter!
  "Execute the call back. Internal Use only"
  [{:keys [quota-reporter]} metrics-headers]
  (when (and @quota-reporter (fn? @quota-reporter))
    (@quota-reporter metrics-headers)))
