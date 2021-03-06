(ns sitemap.core
  "Library for sitemap generation and validation."
  (:use
      [clojure.java.io]
      [hiccup.core :only (html)]
      [hiccup.page :only (xml-declaration)])
  (:require [sitemap.validator :as v]))

; Sitemaps MUST be UTF-8 encoded - http://www.sitemaps.org/faq.html#faq_output_encoding
(def encoding-utf-8 "UTF-8")

(defn- xml-header []
  (html (xml-declaration encoding-utf-8)))


(defn- generate-url-entry [entry]
  [:url
    [:loc (:loc entry)]
    (if (:lastmod entry)
      [:lastmod (:lastmod entry)])
    (if (:changefreq entry)
      [:changefreq (:changefreq entry)])
    (if (:priority entry)
      [:priority (:priority entry)])])


(defn- generate-url-entries [entries]
  (html
     [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
      (map generate-url-entry entries)]))


(defn generate-sitemap
  "Render Clojure data structures to a String of sitemap XML."
  [url-entries]
  (str (xml-header)
       (generate-url-entries url-entries)))


(defn generate-sitemap-and-save
  "Render Clojure data structures to a string of sitemap XML and save it to file."
  [path url-entries]
  (let [sitemap-xml (generate-sitemap url-entries)]
    (spit path sitemap-xml :encoding encoding-utf-8)
    sitemap-xml))


(defn save-sitemap [f sitemap-xml]
  "Save the sitemap XML to a UTF-8 encoded File."
  (doto f
    (spit sitemap-xml :encoding encoding-utf-8)))


(defn validate-sitemap [in]
  "Validate a File, String or InputStream that contains an XML sitemap
   against the sitemaps.org schema and return a list of validation errors. 
   If the Sitemap is valid then the list will be empty. If the XML is 
   structurally invalid then throws SAXParseException."
   (v/validate-sitemap in))
