(ns data.token
  (:require #?(:clj [hato.client :as hc])
            #?(:clj [buddy.sign.jwt :as jwt])
            #?(:clj [data.config :refer [auth-config]])
            [hyperfiddle.electric :as e]))

(def alg #?(:clj (:alg auth-config)))
(def token-api-url #?(:clj (:token-api-url auth-config)))
(def access-token-secret #?(:clj (:access-token-secret auth-config)))

(def access-token-key "access-token")
(def refresh-token-key "refresh-token")

#_{:clj-kondo/ignore [:unused-binding]}
(defn local-storage-get-item [key]
  #?(:cljs (.getItem js/localStorage key)))

#_{:clj-kondo/ignore [:unused-binding]}
(defn local-storage-set-item [key item]
  #?(:cljs (.setItem js/localStorage key item)))

#_{:clj-kondo/ignore [:unused-binding]}
(defn parse-token
  [jwt-secret token]
  {:pre [jwt-secret]}
  #?(:clj
     (try
       (jwt/unsign token jwt-secret {:alg alg})
       (catch Exception _ nil))
     :default nil))

(defn valid? [token]
  (some? (parse-token access-token-secret token)))

(defmacro renew-access-token [& retry-body]
  `(e/client
    (prn :renew-access-token)
    (if-let [refresh-token# (local-storage-get-item ~refresh-token-key)]
      (e/server
       (let [access-token# (get-in (hc/post token-api-url
                                            {:form-params {:grant_type    "refresh_token"
                                                           :refresh_token refresh-token#}
                                             :as          :json}) [:body :access_token])]
         (if access-token#
           (e/client
            (local-storage-set-item ~access-token-key access-token#)
            (prn :refresh-done)
            ~@retry-body)
           {:error "access token refresh failed"})))
      {:error "refresh token not exists"})))

(defmacro authorized
  [& body]
  `(e/client
    (let [access-token# (local-storage-get-item ~access-token-key)]
      (e/server
       (if (valid? access-token#)
         ~@body
         (renew-access-token ~@body))))))

(defn get-user-from-access-token [access-token]
  (parse-token access-token-secret access-token))
