(ns anallist.core
  (:require [gregorstocks.ring-proxy :refer [wrap-proxy]]
            [ring.adapter.jetty :as jetty]
            [clojure.string :as string]))

(defn buttify-string [s]
  (when s
    (-> s
        (string/replace "cloud" "boot boutt")
        (string/replace "buttflare" "cloudflare"))))

(defn buttify [app]
  (fn [request]
    (println "Got request" (:request-method request))
    (if-not (= :get (:request-method request))
      {:body "Butts"
       :status 404}
      (let [{:keys (body) :as result} (dissoc (app request) :cookies)]
        (assoc result :body (buttify-string (when body (slurp body))))))))

(def app (-> (fn [request] {:body "this is only a mirage" :status 404})
             (wrap-proxy "" "http://techcrunch.com/")
             buttify))

(defn -main [& args]
  (jetty/run-jetty (var app) {:port 6969}))
