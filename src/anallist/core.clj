(ns anallist.core
  (:require [tailrecursion.ring-proxy :refer [wrap-proxy]]))

(def app
  (-> routes
      (wrap-proxy "/" "http://angel.co/")
      buttify))
