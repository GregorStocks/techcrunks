(ns anallist.core
  (:require [gregorstocks.ring-proxy :refer [wrap-proxy]]
            [ring.adapter.jetty :as jetty]
            [clojure.string :as string]))

(defn add-gregor-comment [body request]
  (if (or (.endsWith (:uri request) "/")
          (.endsWith (:uri request) "html"))
    (str body "<!-- an Gregor Stocks joint http://gregorsto.cx -->")
    body))

(defn buttify-string [request s]
  (when s
    (-> s
        (string/replace "in the cloud" "up my butt")
        (string/replace "the cloud" "my butt")
        (string/replace "The cloud" "My butt")
        (string/replace "cloud" "butt")
        (string/replace "Cloud" "Butt")
        (string/replace "(?<[.,] )my butt" "the butt")
        (string/replace "(?<[.,] )My butt" "The butt")
        (string/replace "itcoin" "uttcoin")
        (string/replace "runch" "runks")
        (string/replace "crunks20" "crunch20")
        (string/replace "/techcrunks-" "/techcrunch-")
        (string/replace "-crunks" "-crunch")
        (string/replace "ndreessen" "ndresseen") ;; bwahaha
        (string/replace "acebook" "acebutt")
        (string/replace "oinbase" "oinbutt")
        (string/replace "opbox" "optrou")
        (add-gregor-comment request))))

(defn debuttify-uri [s]
  (println "URI" s)
  (-> s
      (string/replace "uttcoin" "itcoin")
      (string/replace "runk" "runch")
      (string/replace "acebutt" "acebook")
      (string/replace "oinbutt" "oinbase")
      (string/replace "butt" "cloud")
      (string/replace "optrou" "opbox")
      (string/replace "ndresseen" "ndreessen")))

(defn buttify [app]
  (fn [request]
    (if-not (= :get (:request-method request))
      {:body "Butts"
       :status 404}
      (let [{:keys (body) :as result} (dissoc (app (update-in request [:uri] debuttify-uri)) :cookies)]
        (assoc result :body (buttify-string request (when body (slurp body))))))))

(def app (-> (fn [request] {:body "this is only a mirage" :status 404})
             (wrap-proxy "" "http://techcrunch.com/")
             buttify))

(defn -main [& args]
  (jetty/run-jetty (var app) {:port 6969}))
