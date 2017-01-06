package com.brine.hai.led.Utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by hai on 23/12/2016.
 */

public class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

    public static String createUrlKeywordSearch(String queryString){
        String url = Config.LOOKUP_DBPEDIA + "QueryClass=&MaxHits=7&QueryString=" + queryString;
        return url;
    }

    public static String createUrlExplode(String uri){
        String query = createQueryExplode(uri);
        String url = "";
        try {
            url = Config.BASE_URL_DBPEDIA + URLEncoder.encode(query, "UTF-8") + Config.RESULT_JSON_TYPE;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showLog(url);
        return url;
    }

    public static String createQueryExplode(String uri){
        String query = Config.PREFIX_DBPEDIA + " \n" +
                "SELECT DISTINCT ?property ?hasValue ?isValueOf\n" +
                "WHERE {\n" +
                "{ <" + uri + "> ?property ?hasValue }\n" +
                "UNION\n" +
                "{ ?isValueOf ?property <" + uri + "> }\n" +
                "FILTER(isIRI(?hasValue) || isIRI(?isValueOf)).\n" +
                "FILTER(?property = dcterms:subject || ?property = skos:broader)\n" +
                "}";
        showLog(query);
        return query;
    }

    public static String createUrlWikiS(String uri1, String uri2){
        String query = createQueryWikiS(uri1, uri2);
        String url = "";
        try {
            url = Config.BASE_URL_DBPEDIA + URLEncoder.encode(query, "UTF-8") + Config.RESULT_JSON_TYPE;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showLog(url);
        return url;
    }

    private static String createQueryWikiS(String uri1, String uri2){
        String query = "";
        //TODO:
        return query;
    }


    public static String createUrlAbstractS(String uri1, String uri2){
        String query = createQueryAbstractS(uri1, uri2);
        String url = "";
        try {
            url = Config.BASE_URL_DBPEDIA + URLEncoder.encode(query, "UTF-8") + Config.RESULT_JSON_TYPE;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showLog(url);
        return url;
    }

    private static String createQueryAbstractS(String uri1, String uri2){
        String query = Config.PREFIX_DBPEDIA + " \n" +
                "prefix dbpedia-owl: <http://dbpedia.org/ontology/>\n" +
                "\n" +
                "\n" +
                "select ?label1, ?label2, ?abtract1, ?abtract2 where\n" +
                "{\n" +
                "  {\n" +
                "     select *\n" +
                "     where{\n" +
                "          <" + uri1 + "> rdfs:label ?label1 ;\n" +
                "                         dbpedia-owl:abstract ?abtract1 .\n" +
                "          FILTER langMatches(lang(?abtract1),'en') . \n" +
                "          FILTER langMatches(lang(?label1),'en') .\n" +
                "     }\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "  {\n" +
                "      select *\n" +
                "      where{\n" +
                "          <" + uri2 + "> rdfs:label ?label2 ;\n" +
                "                         dbpedia-owl:abstract ?abtract2 .\n" +
                "          FILTER langMatches(lang(?label2),'en') . \n" +
                "          FILTER langMatches(lang(?abtract2),'en') .\n" +
                "      }\n" +
                "  }\n" +
                "}";
        showLog(query);
        return query;
    }

    private static void showLog(String message){
        Log.d(TAG, message);
    }
}
