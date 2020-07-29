(defproject imaginathansoft/winkler "0.1.1"
  :description "A moderately paranoid clojurescript library for generating more entropy."
  :url "https://github.com/ndonolli/winkler"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojurescript "1.10.520" :scope "provided"]
   [org.clojure/core.async "1.3.610"]]

  :repositories {"clojars" {:url "https://clojars.org/repo"
                            :sign-releases false}}

  :source-paths
  ["src"])