package com.brine.hai.led.Utils;

/**
 * Created by hai on 25/12/2016.
 */

public class Config {

    public static final String BASE_URL_DBPEDIA = "http://dbpedia.org/sparql?default-graph-uri=&query=";
    public static final String RESULT_JSON_TYPE = "&format=application%2Fsparql-results%2Bjson&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000";
    public static final String LOOKUP_DBPEDIA = "http://lookup.dbpedia.org/api/search/PrefixSearch?";
    public static final String BASE_URL_GOOGLE = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";

    public static final String PREFIX_DBPEDIA =
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                    "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                    "PREFIX : <http://dbpedia.org/resource/>\n" +
                    "PREFIX dbpedia2: <http://dbpedia.org/property/>\n" +
                    "PREFIX dbpedia: <http://dbpedia.org/>\n" +
                    "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                    "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>\n" +
                    "PREFIX dbpprop: <http://www.w3.org/2006/03/wn/wn20/instances/synset-movie-noun-1>\n" +
                    "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                    "PREFIX dbpres: <http://dbpedia.org/resource/>";

    public static final String[] STOP_WORD =
            {
                    "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
                    "any", "are", "aren't", "as", "at", "be", "because", "been", "before",
                    "being", "below", "between", "both", "but", "by", "can't", "cannot",
                    "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing",
                    "don't", "down", "during", "each", "few", "for", "from", "further",
                    "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he",
                    "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
                    "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm",
                    "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself",
                    "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor",
                    "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our",
                    "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd",
                    "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than",
                    "that", "that's", "the", "their", "theirs", "them", "themselves", "then",
                    "there", "there's", "these", "they", "they'd", "they'll", "they're",
                    "they've", "this", "those", "through", "to", "too", "under", "until",
                    "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've",
                    "were", "weren't", "what", "what's", "when", "when's", "where", "where's",
                    "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't",
                    "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your",
                    "yours", "yourself", "yourselves"
            };
}
