(ns com.akolov.mirador.core
  (:require
    [org.httpkit.server :refer :all]
    [clojure.tools.logging :as log]
    )
  (:import (java.nio.file Paths)))

(defn set-interval [callback ms]
  "schedule a function to execute periodically"
  (future (while true (do (Thread/sleep ms) (callback)))))

(defn set-watch-notify [watch notify ms]
  "schedule a watch to be checked periodically"
  (future
    (while true (do (Thread/sleep ms)
                    (when (watch)
                      (notify))))))

(defn file-times [files] (map #(.lastModified %) files))

(defn watcher-files [files]
  "return a function which, on subsequent calls,
  returns true if a file from a list changed"
  (let [last-times (atom (file-times files))]
    (fn []
      (let [new-times (file-times files)]
        (when (not= new-times @last-times)
          (reset! last-times new-times)
          true)))
    )
  )

(defn files-in-folder [fname]
  (letfn [(list-files [folder]
                      (let [files (.listFiles folder)]
                        (concat
                          (filter #(not (.isDirectory %)) files)
                          (flatten (map list-files (filter #(.isDirectory %) files))))))]
    (let [dir (java.io.File. fname)]
      (if (.isDirectory dir)
        (list-files dir)
        (log/warn (str dir " is not a folder in "
                       (.normalize (.toAbsolutePath (Paths/get "." (into-array String []))))
                       ". Will NOT be watched by mirador"
                       ))
        ))))

(defn watcher-folder [folder]
  "return a function which:
  returns true if a file in folder changed, was added or deleted"
  (log/debug "Initializing watcher on folder " folder)
  (let [files (files-in-folder folder)
        last-times (atom (file-times files))]
    (fn []
      (let [files (files-in-folder folder)
            new-times (file-times files)]

        (when (not= new-times @last-times)
          (log/debug "Detected file change!")
          (reset! last-times new-times)
          true)))
    )
  )

(defn combine-watchers [watchers]
  "Combines several watchers into one"
  (fn []
    (reduce (fn [acc watcher] (or acc (watcher))) false watchers)))

(defonce watch-atom (atom {}))


(defn restart-watching [watch notify ms]
  (let [old-future (:future @watch-atom)]
    (when old-future (future-cancel old-future))
    (swap! watch-atom assoc :future
           (set-watch-notify watch notify ms))))


(defn call-browser [channel msg]
  (send! channel msg))

(defn ws-handler [watcher ms]
  (fn [request]
    (with-channel
      request channel
      (on-close channel (fn [status]
                          (log/debug "Killing watch job")
                          (future-cancel (:future @watch-atom))))
      (on-receive channel
                  (fn [_]
                    (log/debug "Started channel")
                    (call-browser channel "started")
                    (restart-watching watcher
                                      #(call-browser channel "reload")
                                      ms))))))

(defn watch-reload [handler & [options]]
  (let [watcher (:watcher options (Exception. "no :watcher specified"))
        ms (:ms options 100)
        uri (:uri options (Exception. ":uri has to be specified"))
        watch-handler (ws-handler watcher ms)]
    (fn [req]
      (if (= uri (:uri req))
        (watch-handler req)
        (handler req)))))

