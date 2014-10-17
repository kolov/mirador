(ns com.akolov.mirador.core-test
  (:require [clojure.test :refer :all]
            [com.akolov.mirador.core :refer :all]
            [midje.sweet :refer :all]
            ))

(fact "scheduled jobs get executed"
      (let [a (atom 1)
            f (set-interval #(swap! a inc) 100)
            w (Thread/sleep 150)
            _ (future-cancel f)
            ]
        @a => 2
        )

      (let [a (atom 1)
            f (set-interval #(swap! a inc) 100)
            w (Thread/sleep 250)
            _ (future-cancel f)
            ]
        @a => 3
        )
      )

(fact "finds files in filders and subfolders"
      (let [files (files-in-folder "src")
            names (map #(.getAbsolutePath %) files)]
        (count files) => 1
        (.endsWith (first names) "mirador/src/com/akolov/mirador/core.clj") => true
        ))