(defproject com.akolov/mirador "0.2.1"
            :description "instant reloading on changes"
            :url "https://github.com/kolov/mirador"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/tools.logging "0.3.1"]
                           [http-kit "2.1.16"]]

            :profiles {
                       :dev {:dependencies [[midje "1.6.3"]
                                            [midje-junit-formatter "0.1.0-SNAPSHOT"]]

                             :plugins      [
                                            [lein-midje "3.0.0"]]}}

            )
