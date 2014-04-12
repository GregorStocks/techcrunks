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
    (if (.endsWith (:uri reqest) "logo.svg")
      (slurp (clojure.java.io/resource "logo.svg"))
      (-> s
          (string/replace "the cloud" "my butt")
          (string/replace "cloud" "butt")
          (string/replace "bitcoin" "buttcoin")
          (string/replace "Tech" "Butt")
          (string/replace "tech" "butt")
          (string/replace "ndreessen" "ndresseen") ;; bwahaha
          (string/replace "acebook" "acebutt")
          (add-gregor-comment request)))))

(defn buttify [app]
  (fn [request]
    (if-not (= :get (:request-method request))
      {:body "Butts"
       :status 404}
      (let [{:keys (body) :as result} (dissoc (app request) :cookies)]
        (assoc result :body (buttify-string request (when body (slurp body))))))))

(def app (-> (fn [request] {:body "this is only a mirage" :status 404})
             (wrap-proxy "" "http://techcrunch.com/")
             buttify))

(defn -main [& args]
  (jetty/run-jetty (var app) {:port 6969}))
