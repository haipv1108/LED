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

    private static void showLog(String message){
        Log.d(TAG, message);
    }
}
