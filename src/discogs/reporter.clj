(ns discogs.reporter)

(defn set-quota-reporter-callback!
  "Attach a callback to the reporter. The function should be arity one.

  If not setup, the client will most likely echo nothing in the logging system.
   "
  {:added "0.1.0"}
  [client callback-fn]
  (update-in client [:quota-reporter]
             #(swap! % (constantly callback-fn))))

(defn compute-reporter!
  "Execute the call back. Internal Use only"
  [{:keys [quota-reporter]} metrics-headers]
  (when (and @quota-reporter (fn? @quota-reporter))
    (@quota-reporter metrics-headers)))
