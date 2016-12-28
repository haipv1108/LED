package com.brine.hai.led;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brine.hai.led.DBpediaRanker.Adapter.KeywordSearchAdapter;
import com.brine.hai.led.DBpediaRanker.Model.GraphS;
import com.brine.hai.led.DBpediaRanker.Model.KeywordSearch;
import com.brine.hai.led.DBpediaRanker.NodeS;
import com.brine.hai.led.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = MainActivity.class.getCanonicalName();

    private static final int MAX_DEPTH = 2;
    private static final float THRESHOLD = 4.0f;
    private static final String GOOGLE = "google";
    private static final String YAHOO = "yahoo";
    private static final String BING = "bing";
    private static final String DILICIOUS = "dilicious";

    private EditText mEdtSearch;
    private ListView mKSListview;

    private KeywordSearchAdapter mKSAdapter;

    private List<String> mSeedURIs;
    private List<KeywordSearch> mKeywordSearchs;
    private List<NodeS> mDiscoveredResources;
    private List<GraphS> mGraphSes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        init();

        abstractS("http://dbpedia.org/resource/Long_Live_Love_(Olivia_Newton-John_song)", "http://dbpedia.org/resource/Without_Your_Love_(André_song)");

        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String word = String.valueOf(editable.toString().trim());
                if(word.length() >= 3){
                    clearLookupResult();
                    lookupUri(word);
                }else{
                    clearLookupResult();
                }
            }
        });

        mKSListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showLogAndToast(mKeywordSearchs.get(i).getUri());
                clearData();
                mSeedURIs.add(mKeywordSearchs.get(i).getUri());
                DBpediaRanker(mSeedURIs);
            }
        });
    }

    private void initUI(){
        mEdtSearch = (EditText) findViewById(R.id.edt_search);
        mKSListview = (ListView) findViewById(R.id.lv_keyword_search);
    }

    private void init(){
        mDiscoveredResources = new ArrayList<>();
        mSeedURIs = new ArrayList<>();
        mKeywordSearchs = new ArrayList<>();
        mGraphSes = new ArrayList<>();

        mKSAdapter = new KeywordSearchAdapter(this, mKeywordSearchs);
        mKSListview.setAdapter(mKSAdapter);
    }

    private void lookupUri(String word){
        String url = Utils.createUrlKeywordSearch(word);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                clearLookupResult();
                parseXmlLookupResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLog(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void parseXmlLookupResult(String response){
        if(response == null) return;
        InputStream inputStream = new ByteArrayInputStream(
                response.getBytes(StandardCharsets.UTF_8));

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("Result");
            for(int i = 0; i < nList.getLength(); i++){
                Node node = nList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element e = (Element) node;
                    String label = e.getElementsByTagName("Label").item(0)
                            .getChildNodes().item(0).getNodeValue();
                    String uri = e.getElementsByTagName("URI").item(0)
                            .getChildNodes().item(0).getNodeValue();
                    KeywordSearch ks = new KeywordSearch(label, uri);
                    updateLookupResult(ks);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private void clearLookupResult(){
        mKeywordSearchs.clear();
        mKSAdapter.notifyDataSetChanged();
    }

    private void updateLookupResult(KeywordSearch ks){
        mKeywordSearchs.add(ks);
        mKSAdapter.notifyDataSetChanged();
    }

    private void clearData(){
        mSeedURIs.clear();
        mGraphSes.clear();
    }

    /* ===========================================================================================*/
                    /* Algorithm 1: DBpediaRanker */

    private void DBpediaRanker(List<String> seedURIs){
        for(String uri : seedURIs){
            NodeS r = new NodeS(uri, 1, false, true);
            if(!isURIExist(r))
                mDiscoveredResources.add(r);
        }

        boolean finished = false;
        while (!finished){
            for(NodeS nodeS : mDiscoveredResources){
                if(nodeS.isIn_context() && !nodeS.isRanked()){
                    nodeS.setRanked(true);
                    explore(nodeS.getUri(), nodeS.getUri(), MAX_DEPTH);
                }
            }
            finished = true;
            for(NodeS nodeS : mDiscoveredResources){
                if(!nodeS.isIn_context()){
                    if(is_in_context(nodeS.getUri())){
                        nodeS.setIn_context(true);
                        finished = false;
                    }
                }
            }
        }
    }

    private boolean isURIExist(NodeS node){
        for(NodeS nodeS : mDiscoveredResources){
            if(nodeS.getUri().equals(node.getUri()))
                return true;
        }
        return false;
    }

    /* ==========================================================================================*/
            /* Algorithm 2: explore(). The main function implemented in Graph Explorer. */

    private void explore(String root, String uri, int depth){
        float sim = 0.0f;

        if(depth < MAX_DEPTH){
            NodeS nodeS = searchUriInR(uri);
            if(nodeS != null){
                nodeS.setHits(nodeS.getHits() + 1);
                if(is_in_context(uri)){
                    sim = similarity(root, uri);
                }
            }else{
                NodeS node = new NodeS(uri, 1, false);
                mDiscoveredResources.add(node);
                if(is_in_context(uri)){
                    sim = similarity(root, uri);
                    node.setIn_context(true);
                }else{
                    node.setIn_context(false);
                }
            }
        }

        if(depth > 0){
            explode(root, uri, depth, sim);
        }
    }

    private NodeS searchUriInR(String uri){
        for(NodeS nodeS : mDiscoveredResources){
            if(nodeS.getUri().equals(uri)){
                return nodeS;
            }
        }
        return null;
    }

    private void explode(final String root, final String uri, final int depth, final float sim){
        String url = Utils.createUrlExplode(uri);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> relatedUris = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONObject("results").getJSONArray("bindings");
                    if(data.length() == 0){
                        showLogAndToast("No result");
                    }else{
                        for(int i = 0; i < data.length(); i++){
                            JSONObject element = data.getJSONObject(i);
                            String uriResult = element.getJSONObject("hasValue").getString("value");
                            relatedUris.add(uriResult);
                        }

                        for(String n : relatedUris){
                            explore(root, n, depth - 1);
                            showLogAndToast(n);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLog(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(request);
        mGraphSes.add(new GraphS(root, uri, sim));
    }

    /* ==========================================================================================*/
        /* Algorithm 3: similarity(uri1, uri2). The main function implemented in Ranker. */

    private float similarity(String uri1, String uri2){
        float wikipediaS = wikiS(uri1, uri2);
        float abstractS = abstractS(uri1, uri2);
        float googleS = engineS(uri1, uri2, GOOGLE);
        float yahooS = engineS(uri1, uri2, YAHOO);
        float bingS = engineS(uri1, uri2, BING);
        float diliciousS = engineS(uri1, uri2, DILICIOUS);
        return wikipediaS + abstractS + googleS + yahooS + bingS + diliciousS;
    }

    private float wikiS(String uri1, String uri2){
        //TODO:
        return 0.0f;
    }

    private float abstractS(String uri1, String uri2){
        String url = Utils.createUrlAbstractS(uri1, uri2);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                float abS = 0.0f;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONObject("results").getJSONArray("bindings");
                    if(data.length() == 0){
                        showLogAndToast("No result");
                    }else{
                        JSONObject element = data.getJSONObject(0);
                        String label1 = element.getJSONObject("label1").getString("value");
                        String abtract1 = element.getJSONObject("abtract1").getString("value");
                        String label2 = element.getJSONObject("label2").getString("value");
                        String abtract2 = element.getJSONObject("abtract2").getString("value");
                        abS = calWordContained(label1, abtract2) + calWordContained(label2, abtract1);
                        //TODO: abS
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLog(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(request);
        return 0.0f;
    }

    private float calWordContained(String label, String abtract){
        if(label.length() == 0 || abtract.length() == 0){
            return 0.0f;
        }
        List<String> words = Arrays.asList(label.split(" "));
        int count = 0;
        float length = words.size();
        for(int i = 0; i < length; i++){
            if(abtract.toLowerCase().contains(words.get(i).toLowerCase())){
                count++;
            }
        }
        return (count/length);
    }

    private float engineS(String uri1, String uri2, String type){
        switch (type){
            case GOOGLE:

                break;
            case YAHOO:
                break;
            case BING:
                break;
            case DILICIOUS:
                break;
        }


        return 0.0f;
    }

    private void googleResults(String label){
        //Customsearch customsearch =
    }

    /* ==========================================================================================*/
        /* Algorithm 4: is_in_context(r). The main function implemented in Context Analyzer */
    private boolean is_in_context(String uri){
        float s = 0.0f;
        List<String> Context = getContext();
        for(String r: Context){
            s += similarity(uri, r);
            if(s >= THRESHOLD){
                return true;
            }
        }
        return false;
    }

    private List<String> getContext(){
        //TODO 3: getContext -> done
        return null;
    }

    /*============================================================================================*/

    private void showLog(String message){
        Log.d(TAG, message);
    }

    private void showLogAndToast(final String message){
        showLog(message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
