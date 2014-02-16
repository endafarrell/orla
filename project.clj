(defproject orla "0.2.0-SNAPSHOT"
  :description "Simple diabetes data management app."
  :url "http://endafarrell.net"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.0-alpha5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [log4j/log4j "1.2.17"]
                 [commons-logging/commons-logging "1.1"]
                 [commons-fileupload/commons-fileupload "1.3"]
                 [compojure "1.1.6"]
                 [ring/ring-jetty-adapter "1.0.0"]
                 [ring-middleware-format "0.3.2"]]
  :plugins [[lein-ring "0.8.8"]]
  :ring {:handler endafarrell.orla.handler/app}
  :aot [endafarrell.orla.handler]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}}
  ;;; Filesystem Paths
  ;; In order to support Maven builds, we configure our directories to be the same
  ;; as Maven's defaults.
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"] ; Java source is stored separately.
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resource"]
  :main endafarrell.orla.handler)
