(defproject io.trosa/discogs "0.1.5-SNAPSHOT"
  :description "A data-oriented Discogs API Client"
  :url "https://github.com/iomonad/discogs"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :deploy-repositories [["snapshots" {:url "https://repo.clojars.org"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://repo.clojars.org"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]
  :dependencies [[org.clojure/clojure "1.12.4"]
                 [org.clojure/tools.logging "1.3.1"]
                 [clj-http "3.13.1"]
                 [metosin/jsonista "0.3.13"]
                 [metosin/malli "0.20.0"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.5.1"]
                                  [spootnik/unilog "0.7.32"]]
                   :source-paths ["dev"]
                   :plugins [[lein-ancient "1.0.0-RC3"]]
                   :resource-paths ["dev-resources"]
                   :repl-options {:init-ns user
                                  :prompt #(str "\u001B[35m[\u001B[34m" % "\u001B[35m]\u001B[33m▶\u001B[m ")}}})
