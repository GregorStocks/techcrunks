(ns anallist.core
  (:require [gregorstocks.ring-proxy :refer [wrap-proxy]]
            [ring.adapter.jetty :as jetty]
            [clojure.string :as string]
            [clj-time.core :as time]))

(defn add-gregor-comment [body request]
  (cond (or (.endsWith (:uri request) "/")
            (.endsWith (:uri request) "html"))
              (str body "<!-- an Gregor Stocks joint http://gregorsto.cx -->")
        (.endsWith (:uri request) "robots.txt")
          "UserAgent *\nDisallow: /"
        :else body))

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
        (string/replace #"acebook(?![.])" "acebutt")
        (string/replace "oinbase" "oinbutt")
        (string/replace "opbox" "optrou")
        (string/replace "quare" "hart")
        (string/replace "isrupt" "iSrUpT")
        (string/replace "heartbleed" "buttbleed")
        (string/replace "Heartbleed" "Buttbleed")
        (add-gregor-comment request))))

(defn debuttify-uri [s]
  (-> s
      (string/replace "uttcoin" "itcoin")
      (string/replace "runk" "runch")
      (string/replace "buttbleed" "heartbleed")
      (string/replace "Buttbleed" "Heartbleed")
      (string/replace "acebutt" "acebook")
      (string/replace "oinbutt" "oinbase")
      (string/replace "butt" "cloud")
      (string/replace "optrou" "opbox")
      (string/replace "shart" "square")
      (string/replace "Shart" "Square")
      (string/replace "iSrUpT" "isrupt")
      (string/replace "ndresseen" "ndreessen")))

(defn buttify [app]
  (fn [request]
    (println (time/now) (:uri request) (-> request :headers (get "x-real-ip")) (-> request :headers (get "user-agent")))
    (let [{:keys (body) :as result} (dissoc (app (update-in request [:uri] debuttify-uri)) :cookies)]
      (assoc result :body (buttify-string request (when body (slurp body)))))))

(def app (-> (fn [request] {:body "this is only a mirage" :status 404})
             (wrap-proxy "" "http://techcrunch.com/")
             buttify))

(defn -main [& args]
  (jetty/run-jetty (var app) {:port 6969}))
