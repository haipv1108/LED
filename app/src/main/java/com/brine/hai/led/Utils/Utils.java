package com.brine.hai.led.Utils;

import android.util.Log;

import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import static com.brine.hai.led.Utils.Config.BASE_URL_DBPEDIA;
import static com.brine.hai.led.Utils.Config.RESULT_JSON_TYPE;

/**
 * Created by hai on 23/12/2016.
 */

public class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

    /*------------------------- Faceted Search Dbpedia ----------------------*/
    public static String createUrlFacetedSearch(String keyword, String optionSearch){
        String query = createQueryFacetedSearch(keyword, optionSearch);
        String url = "";
        try {
            url = BASE_URL_DBPEDIA + URLEncoder.encode(query, "UTF-8") + RESULT_JSON_TYPE;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showLog(url);
        return url;
    }

    private static String createQueryFacetedSearch(String keyword, String optionSearch){
        List<String> listWord = Arrays.asList(keyword.split(" "));
        String bifVectorParams = getBifVectorParams(keyword);
        String bifContainParams = getBifContainParams(keyword);

        String query = "     select ?s1 as ?c1, (bif:search_excerpt (bif:vector (" + bifVectorParams + "), ?o1)) as ?c2, ?sc, ?rank, ?g where {{{ select ?s1, (?sc * 3e-1) as ?sc, ?o1, (sql:rnk_scale (<LONG::IRI_RANK> (?s1))) as ?rank, ?g where  \n" +
                "  { \n" +
                "    quad map virtrdf:DefaultQuadMap \n" +
                "    { \n" +
                "      graph ?g \n" +
                "      { \n" +
                "         ?s1 ?s1textp ?o1 .\n" +
                "        ?o1 bif:contains  '(" + bifContainParams + ")'  option (score ?sc)  .\n" +
                "        \n" +
                "      }\n" +
                "     }\n" +
                "    "  + optionSearch + "\n" +
                "  }\n" +
                " order by desc (?sc * 3e-1 + sql:rnk_scale (<LONG::IRI_RANK> (?s1)))  limit 50  offset 0 }}} ";
        showLog(query);
        return query;
    }

    private static String getBifContainParams(String keywordSearch){
        List<String> listWord = Arrays.asList(keywordSearch.split(" "));
        String bifContainParams = "";
        for(int i = 0; i < listWord.size(); i++){
            if(bifContainParams.length() == 0){
                bifContainParams += listWord.get(i).toUpperCase();
            }else {
                bifContainParams += " AND " + listWord.get(i).toUpperCase();
            }
        }
        showLog("Contain param: " + bifContainParams);
        return bifContainParams;
    }

    private static String getBifVectorParams(String keywordSearch){
        List<String> listWord = Arrays.asList(keywordSearch.split(" "));
        String bifVectorParams = "";
        for(int i = 0; i < listWord.size(); i++){
            if(bifVectorParams.length() == 0){
                bifVectorParams += "'" + listWord.get(i).toUpperCase() + "'";
            }else {
                bifVectorParams += ", " + "'" + listWord.get(i).toUpperCase() + "'";
            }
        }
        showLog("Vector param: " + bifVectorParams);
        return bifVectorParams;
    }
    /*----------------------------------------------------------------------*/

    /*---------------------------------LOOKUP URI---------------------------*/
    public static String createUrlKeywordSearch(String queryString){
        String url = Config.LOOKUP_DBPEDIA + "QueryClass=&MaxHits=7&QueryString=" + queryString;
        return url;
    }

    /*-----------------------------------------------------------------------*/
    /*---------------------------SLIDING WINDOW------------------------------*/
    public static String createUrlSearchAccuracyEntity(String keyword){
        String query = searchAccuracyEntitiesQuery(keyword);
        String url = createUrlDbpedia(query);
        return url;
    }

    public static String searchAccuracyEntitiesQuery(String keyword){
        String uri = convertKeywordToUri(keyword);
        String queryString =
                Config.PREFIX_DBPEDIA +
                        "\n" +
                        "SELECT *\n" +
                        "WHERE{\n" +
                        "  {?s a dbpedia-owl:Song ;\n" +
                        "      rdfs:label \"" + keyword + "\" . \n" +
                        "  }\n" +
                        "UNION{\n" +
                        "   ?s dbpedia2:name <" + uri + "> .\n" +
                        " }\n" +
                        " UNION{\n" +
                        "   ?s dbpedia-owl:album <" + uri + "> .\n" +
                        " }\n" +
                        " UNION{\n" +
                        "   ?s dbpedia-owl:artist <" + uri + "> .\n" +
                        " }\n" +
                        " UNION{\n" +
                        "  ?s dbpedia-owl:composer <" + uri + "> .\n" +
                        " }\n" +
                        " UNION{\n" +
                        "  ?s dbpedia-owl:genre <" + uri + "> .\n" +
                        " }\n" +
                        " UNION{\n" +
                        "  ?s dbpedia-owl:lyrics <" + uri + "> .\n" +
                        " }\n" +
                        " UNION{\n" +
                        "  ?s dbpedia-owl:producer <" + uri + "> .\n" +
                        " }\n" +
                        " UNION{\n" +
                        "  ?s dbpedia-owl:writer <" + uri + "> .\n" +
                        " }\n" +
                        " ?s rdfs:label ?label .\n" +
                        " ?s dbpedia-owl:abstract ?abtract .\n" +
                        " FILTER langMatches(lang(?label), \"en\")\n" +
                        " FILTER langMatches(lang(?abtract), \"en\")\n" +
                        "}\n" +
                        "LIMIT 20";
        //ToDO; chi lay tieng anh
        showLog("searchAccuracyEntitiesQuery: \n" + queryString);
        return queryString;
    }

    public static String createUrlSearchReleatedEntity(String keyword){
        String query = searchRelatedEntitiesQuery(keyword);
        String url = createUrlDbpedia(query);
        return url;
    }

    public static String searchRelatedEntitiesQuery(String keyword){
        String queryString = Config.PREFIX_DBPEDIA +
                "SELECT distinct *\n" +
                "WHERE{\n" +
                " ?s a dbpedia-owl:Song ;\n" +
                "        rdfs:label ?label ;\n" +
                "        dbpedia-owl:abstract ?abtract . \n" +
                " FILTER regex(?label, \"" + keyword + "\",'i'). \n" +
                " FILTER langMatches( lang(?label), \"en\" )\n" +
                "}\n" +
                "LIMIT 16";
        return queryString;
    }

    private static String convertKeywordToUri(String keyword){
        List<String> listWord = Arrays.asList(keyword.split(" "));
        String converted = "";
        for(int i = 0; i < listWord.size(); i++){
            String firstChar = listWord.get(i).substring(0, 1);
            String wordConverted = listWord.get(i).replaceFirst(firstChar, firstChar.toUpperCase());
            if(i + 1 < listWord.size()){
                converted += wordConverted + "_";
            }else{
                converted += wordConverted;
            }
        }
        String uri = "http://dbpedia.org/resource/" + converted;
        showLog("uri converted: " + uri);
        return uri;
    }
    /*------------------------------------------------------------------------*/

    public static String createUrlExplode(String uri){
        String query = createQueryExplode(uri);
        String url = createUrlDbpedia(query);
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
                "} LIMIT 10";
        showLog(query);
        return query;
    }

    public static String createUrlWikiS(String uri1, String uri2){
        String query = createQueryWikiS(uri1, uri2);
        String url = createUrlDbpedia(query);
        return url;
    }

    private static String createQueryWikiS(String uri1, String uri2){
        String query = Config.PREFIX_DBPEDIA + " \n" +
                "select * where{\n" +
                "  {  \n" +
                "     select ?p1 {\n" +
                "         <" + uri1 + "> ?p1 <" + uri2 + "> .\n" +
                "         FILTER regex(?p1, \"link\", \"i\")\n" +
                "     }\n" +
                "  }UNION{\n" +
                "           select ?p2 {\n" +
                "           <" + uri2 + "> ?p2 <" + uri1 + "> .\n" +
                "           FILTER regex(?p2, \"link\", \"i\")\n" +
                "           }\n" +
                "   }\n" +
                "}\n" +
                "\n";
        return query;
    }


    public static String createUrlAbstractS(String uri1, String uri2){
        String query = createQueryAbstractS(uri1, uri2);
        String url = createUrlDbpedia(query);
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

    private static String createUrlDbpedia(String query){
        String url = "";
        try {
            url = BASE_URL_DBPEDIA + URLEncoder.encode(query, "UTF-8") + RESULT_JSON_TYPE;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showLog(url);
        return url;
    }

    private static void showLog(String message){
        Log.d(TAG, message);
    }
}
