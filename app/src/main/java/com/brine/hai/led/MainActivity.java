package com.brine.hai.led;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brine.hai.led.Adapter.FSAdapter;
import com.brine.hai.led.Adapter.KeywordSearchAdapter;
import com.brine.hai.led.Adapter.SLDAdapter;
import com.brine.hai.led.DBpediaRanker.Model.GraphS;
import com.brine.hai.led.DBpediaRanker.Model.KeywordSearch;
import com.brine.hai.led.DBpediaRanker.Model.SLDResult;
import com.brine.hai.led.DBpediaRanker.NodeS;
import com.brine.hai.led.Model.FSResult;
import com.brine.hai.led.Utils.Config;
import com.brine.hai.led.Utils.DbpediaMusicConstant;
import com.brine.hai.led.Utils.Utils;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;

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

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, FSAdapter.FSCallBack, SLDAdapter.SLDCallBack{

    public static final String TAG = MainActivity.class.getCanonicalName();

    private static final int LOOKUP_URI = 1;
    private static final int FACTED_SEARCH = 2;
    private static final int SLIDING_WINDOW = 3;

    private static final int MAX_DEPTH = 2;
    private static final float THRESHOLD = 4.0f;
    private static final String GOOGLE = "google";
    private static final String YAHOO = "yahoo";
    private static final String BING = "bing";
    private static final String DILICIOUS = "dilicious";

    private EditText mEdtSearch;
    private ImageView mImgSearchOption, mImgSearch, mImgEXSearch;
    private ListView mKSListview;
    private RecyclerView mRecyclerFS, mRecyclerSLD;
    private TagView mTagGroup;

    /*-----Lookup uri-------*/
    private KeywordSearchAdapter mKSAdapter;
    private List<KeywordSearch> mKeywordSearchs;

    /*-----Facted search-----*/
    private FSAdapter mFSAdapter;
    private List<FSResult> mFSResults;

    /*-----Sliding window----*/
    private List<SLDResult> mSLDResults;
    private SLDAdapter mSLDAdapter;
    private List<String> mEntities;

    /*-----Dbpedia ranker-----*/
    private List<String> mSeedURIs;
    private List<String> mInputURIs;
    private List<NodeS> mDiscoveredResources;
    private List<GraphS> mGraphSes;

    private int typeSearch = LOOKUP_URI;

    private interface TypeSearchCallBack {
        void changeTypeSearch();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        init();
        if(typeSearch == LOOKUP_URI){
            listeningEdtSearch();
        }
        listeningTagGroup();
    }

    private void initUI(){
        getSupportActionBar().hide();
        mEdtSearch = (EditText) findViewById(R.id.edt_search);
        mKSListview = (ListView) findViewById(R.id.lv_keyword_search);
        mRecyclerFS = (RecyclerView)findViewById(R.id.recycle_fsresult);
        mRecyclerSLD = (RecyclerView) findViewById(R.id.recycle_sldresult);
        mImgSearchOption = (ImageView) findViewById(R.id.img_search_option);
        mImgSearch = (ImageView) findViewById(R.id.img_search);
        mImgEXSearch = (ImageView) findViewById(R.id.img_ex_search);
        mTagGroup = (TagView) findViewById(R.id.tag_group_uri);

        mImgSearchOption.setOnClickListener(this);
        mImgSearch.setOnClickListener(this);
        mImgEXSearch.setOnClickListener(this);
    }

    private void init(){
        /*------Look up uri---------*/
        mKeywordSearchs = new ArrayList<>();
        mKSAdapter = new KeywordSearchAdapter(this, mKeywordSearchs);
        mKSListview.setAdapter(mKSAdapter);

        /*------Facted search-------*/
        mFSResults = new ArrayList<>();
        mFSAdapter = new FSAdapter(getApplicationContext(), mFSResults, this);

        mRecyclerFS.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerFS =
                new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false);
        mRecyclerFS.setLayoutManager(layoutManagerFS);
        mRecyclerFS.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        mRecyclerFS.setItemAnimator(new DefaultItemAnimator());
        mRecyclerFS.setAdapter(mFSAdapter);

        /*------Sliding window-------*/
        mSLDResults = new ArrayList<>();
        mEntities = new ArrayList<>();
        mSLDAdapter = new SLDAdapter(this, mSLDResults, this);

        mRecyclerSLD.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerSLD =
                new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false);
        mRecyclerSLD.setLayoutManager(layoutManagerSLD);
        mRecyclerSLD.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        mRecyclerSLD.setItemAnimator(new DefaultItemAnimator());
        mRecyclerSLD.setAdapter(mSLDAdapter);

        /*-------DBpedia ranker------*/
        mDiscoveredResources = new ArrayList<>();
        mInputURIs = new ArrayList<>();
        mSeedURIs = new ArrayList<>();
        mGraphSes = new ArrayList<>();
    }

    private TypeSearchCallBack callBack = new TypeSearchCallBack() {
        @Override
        public void changeTypeSearch() {
            resetSearch();
            switch (typeSearch){
                case LOOKUP_URI:
                    mKSListview.setVisibility(View.VISIBLE);
                    mRecyclerFS.setVisibility(View.GONE);
                    mRecyclerSLD.setVisibility(View.GONE);
                    listeningEdtSearch();
                    break;
                case FACTED_SEARCH:
                    mKSListview.setVisibility(View.GONE);
                    mRecyclerFS.setVisibility(View.VISIBLE);
                    mRecyclerSLD.setVisibility(View.GONE);
                    break;
                case SLIDING_WINDOW:
                    mKSListview.setVisibility(View.GONE);
                    mRecyclerFS.setVisibility(View.GONE);
                    mRecyclerSLD.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    private void resetSearch(){
        mEdtSearch.setText("");
    }

    private void listeningEdtSearch(){
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(typeSearch == LOOKUP_URI){
                    String word = String.valueOf(editable.toString().trim());
                    if(word.length() >= 3){
                        clearLookupResult();
                        lookupUri(word);
                    }else{
                        clearLookupResult();
                    }
                }
            }
        });

        mKSListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addTagGroup(mKeywordSearchs.get(i).getLabel(), mKeywordSearchs.get(i).getUri());
                clearLookupResult();
                mEdtSearch.setText("");
            }
        });
    }

    private void addTagGroup(String label, String uri){
        if(!mInputURIs.contains(uri)){
            Tag tag = new Tag(label);
            tag.isDeletable = true;
            mTagGroup.addTag(tag);
            mInputURIs.add(uri);
        }else{
            showLogAndToast("Added");
        }
    }

    private void listeningTagGroup(){
        mTagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(TagView tagView, Tag tag, int i) {
                mTagGroup.remove(i);
                mInputURIs.remove(i);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_search:
                String keywords = getKeywordInput();
                if(keywords != null){
                    if(typeSearch == FACTED_SEARCH){
                        facetedSearch(keywords, "");
                    }else {
                        slidingWindow(keywords);
                    }
                }
                break;
            case R.id.img_search_option:
                showPopupSearchOption();
                break;
            case R.id.img_ex_search:
                clearData();
                if(mInputURIs.isEmpty()){
                    showLogAndToast("Please choice input uri");
                }else{
                    mSeedURIs.addAll(mInputURIs);
                    showLogAndToast(mSeedURIs.toString());
                    DBpediaRanker(mSeedURIs);
                }
                break;
        }
    }

    private String getKeywordInput(){
        String keywords = mEdtSearch.getText().toString().trim();
        if(keywords.length() == 0){
            mEdtSearch.setError("Keyword cannot empty!");
            return null;
        }
        return keywords;
    }

    private void showPopupSearchOption(){
        PopupMenu popupMenu = new PopupMenu(this, mImgSearchOption);
        popupMenu.getMenuInflater().inflate(R.menu.menu_search_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_lookup_uri:
                        if(typeSearch != LOOKUP_URI){
                            typeSearch = LOOKUP_URI;
                            callBack.changeTypeSearch();
                        }
                        return true;
                    case R.id.menu_facted_search:
                        if(typeSearch != FACTED_SEARCH){
                            typeSearch = FACTED_SEARCH;
                            callBack.changeTypeSearch();
                        }
                        return true;
                    case R.id.menu_sliding_window:
                        if(typeSearch != SLIDING_WINDOW){
                            typeSearch = SLIDING_WINDOW;
                            callBack.changeTypeSearch();
                        }
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    /*--------------LOOKUP URI------------------------*/
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
        AppController.getInstance().addToRequestQueue(stringRequest, "lookup");
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

    /*-------------------------------------------------------------*/

    /*---------------------FACTED SEARCH---------------------------*/
    private void facetedSearch(String keywords, String optionSearch){
        mFSResults.clear();
        mFSAdapter.notifyDataSetChanged();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = Utils.createUrlFacetedSearch(keywords, optionSearch);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                parsefacetedSeachResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                showLog(error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    private void parsefacetedSeachResult(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray data = jsonObject.getJSONObject("results").getJSONArray("bindings");
            if(data.length() == 0){
                showLogAndToast("No results");
            }else{
                for(int i = 0; i < data.length(); i++){
                    JSONObject element = data.getJSONObject(i);
                    String uri = element.getJSONObject("c1").getString("value");

                    List<String> splitUri = Arrays.asList(uri.split("/"));
                    String localName = splitUri.get(splitUri.size()-1);
                    String label = localName.replace("_", " ");

                    String description = element.getJSONObject("c2").getString("value");
                    double score = element.getJSONObject("sc").getDouble("value");
                    double rank = element.getJSONObject("rank").getDouble("value");

                    FSResult result = new FSResult(uri, label, description, score, rank);
                    updateFSResult(result);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateFSResult(FSResult result){
        mFSResults.add(result);
        mFSAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDetailsUriFS(String uri) {
        showLogAndToast(uri);
        //TODO: show details uri
    }

    @Override
    public void addSearchExploratoryFS(String label, String uri) {
        addTagGroup(label, uri);
    }

    /*=======================SLIDING WINDOW=====================*/
    private void slidingWindow(String keyword){
        mSLDResults.clear();
        mSLDAdapter.notifyDataSetChanged();
        List<String> splitKeywords = splitPhaseKeywordSLD(keyword);
        findEntityKeywordSLD(keyword);
        searchAccuracyEntities(splitKeywords);
//        searchReleatedEntities(splitKeywords);
    }

    private List<String> splitPhaseKeywordSLD(String keyword){
        List<String> phaseKeywords = new ArrayList<>();
        List<String> listWord = Arrays.asList(keyword.split(" "));
        int lengthKeyword = listWord.size();
        for(int i = 0; i < lengthKeyword; i++){
            if (i + 2 < lengthKeyword) {
                String pharse = listWord.get(i) + " " +
                        listWord.get(i + 1) + " " + listWord.get(i + 2);
                phaseKeywords.add(pharse);
            }
            if(i + 1 < lengthKeyword){
                String pharse = listWord.get(i) + " " + listWord.get(i + 1);
                phaseKeywords.add(pharse);
            }
            if(!isStopWord(listWord.get(i))){
                phaseKeywords.add(listWord.get(i));
            }
        }
        showLog("Phase Keyword: " + phaseKeywords.toString());
        return phaseKeywords;
    }

    private boolean isStopWord(String word){
        List<String> listStopWord = Arrays.asList(Config.STOP_WORD);
        return listStopWord.contains(word);
    }

    private void findEntityKeywordSLD(String keyword){
        mEntities.clear();
        List<String> musicProperties = DbpediaMusicConstant.getMusicProperties();
        List<String> listWord = Arrays.asList(keyword.split(" "));

        for(int i = 0; i < musicProperties.size(); i++){
            if(checkOneWord(musicProperties.get(i), listWord)){
                showLog("checkOneword: " + musicProperties.get(i));
                String entity = "http://dbpedia.org/property/" + musicProperties.get(i);
                if(!mEntities.contains(entity)){
                    mEntities.add(entity);
                }
            } else if(checkTwoWord(musicProperties.get(i), listWord)){
                showLog("checkTwoword: " + musicProperties.get(i));
                String entity = "http://dbpedia.org/property/" + musicProperties.get(i);
                if(!mEntities.contains(entity)){
                    mEntities.add(entity);
                }
            }
        }
        showLog("mentities: " + mEntities.toString());
    }

    private boolean checkOneWord(String entityLabel, List<String> listWord){
        for(int i = 0; i < listWord.size(); i++){
            if(listWord.get(i).toLowerCase().equals(entityLabel.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private boolean checkTwoWord(String entityLabel, List<String> listWord){
        int length = listWord.size();
        for(int i = 0; i < length; i++){
            if(i + 1 < length){
               String word = listWord.get(i) + listWord.get(i + 1);
                if(word.toLowerCase().equals(entityLabel.toLowerCase())){
                    return true;
                }
            }
        }
        return false;
    }

    private void searchAccuracyEntities(List<String> splitKeywords){
        for(String keyword : splitKeywords){
            String url = Utils.createUrlSearchAccuracyEntity(keyword);
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray data = jsonObject.getJSONObject("results").getJSONArray("bindings");
                        if(data.length() == 0){
                            showLog("No result");
                        }else{
                            for(int i = 0; i < data.length(); i++){
                                JSONObject element = data.getJSONObject(i);
                                String uri = element.getJSONObject("s").getString("value");
                                String label = element.getJSONObject("label").getString("value");
                                String abtract = element.getJSONObject("abtract").getString("value");
                                SLDResult sldResult = new SLDResult(uri, label, abtract);
                                updateSLDResult(sldResult);
                                showLog("ACCURACY uri: " + uri + "---label: " + label);
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
            AppController.getInstance().addToRequestQueue(request, "accuracy");
        }
    }

    private void searchReleatedEntities(List<String> splitKeyword){
        for(String keyword : splitKeyword){
            String url = Utils.createUrlSearchReleatedEntity(keyword);
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray data = jsonObject.getJSONObject("results").getJSONArray("bindings");
                        if(data.length() == 0){
                            showLog("No result");
                        }else{
                            for(int i = 0; i < data.length(); i++){
                                JSONObject element = data.getJSONObject(i);
                                String uri = element.getJSONObject("s").getString("value");
                                String label = element.getJSONObject("label").getString("value");
                                String abtract = element.getJSONObject("abtract").getString("value");
                                showLog("RELEATED: uri: " + uri + "--- label: " + label);
                                SLDResult sldResult = new SLDResult(uri, label, abtract);
                                updateSLDResult(sldResult);
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
            AppController.getInstance().addToRequestQueue(request, "releated");
        }
    }

    private void searchExpandEntities(){
        showLogAndToast(mEntities.toString());
    }

    private void updateSLDResult(SLDResult sldResult){
        mSLDResults.add(sldResult);
        mSLDAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDetailsUriSLD(String uri) {

    }

    @Override
    public void addSearchExploratorySLD(String label, String uri) {

    }

    /*-----------------------------------------------------------*/

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
                if(nodeS.isInContext() && !nodeS.isRanked()){
                    nodeS.setRanked(true);
                    explore(nodeS.getUri(), nodeS.getUri(), MAX_DEPTH);
                }
            }
            finished = true;
            for(NodeS nodeS : mDiscoveredResources){
                if(!nodeS.isInContext()){
                    if(is_in_context(nodeS.getUri())){
                        nodeS.setInContext(true);
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

    private void explore(final String root, String uri, final int depth){
        if(depth < MAX_DEPTH){
            float sim = 0.0f;
            NodeS nodeS = searchUriInR(uri);
            if(nodeS != null){
                nodeS.setHits(nodeS.getHits() + 1);
                if(is_in_context(uri)){
                    sim = similarity(root, uri);
                }
            }else{
                NodeS node = new NodeS(uri, 1, false, false);
                mDiscoveredResources.add(node);
                if(is_in_context(uri)){
                    sim = similarity(root, uri);
                    node.setInContext(true);
                }else{
                    node.setInContext(false);
                }
            }
            mGraphSes.add(new GraphS(root, uri, sim));
        }

        if(depth > 0){
            String url = Utils.createUrlExplode(uri);
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    List<String> relatedUris = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray data = jsonObject.getJSONObject("results").getJSONArray("bindings");
                        if(data.length() == 0){
                            showLog("No result");
                        }else{
                            for(int i = 0; i < data.length(); i++){
                                JSONObject element = data.getJSONObject(i);
                                String uriResult = element.getJSONObject("hasValue").getString("value");
                                relatedUris.add(uriResult);
                            }

                            for(String n : relatedUris){
                                explore(root, n, depth - 1);
                                showLog(n);
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
            AppController.getInstance().addToRequestQueue(request, "explode");
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

    /* ==========================================================================================*/
        /* Algorithm 3: similarity(uri1, uri2). The main function implemented in Ranker. */

    private float similarity(String uri1, String uri2){
        //float wikipediaS = wikiS(uri1, uri2);
        //float abstractS = abstractS(uri1, uri2);
//        float googleS = engineS(uri1, uri2, GOOGLE);
//        float yahooS = engineS(uri1, uri2, YAHOO);
//        float bingS = engineS(uri1, uri2, BING);
//        float diliciousS = engineS(uri1, uri2, DILICIOUS);
        //showLog("SIMILARITY: " + total);
        //return wikipediaS + abstractS + googleS + yahooS + bingS + diliciousS;
        return 7.0f;
    }

    private void wikiS(final String uri1, final String uri2){
        String url = Utils.createUrlWikiS(uri1, uri2);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                float wikiS = 0.0f;
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray data = json.getJSONObject("results").getJSONArray("bindings");
                    if(data.length() == 0){
                        showLog("ResponseWikiS: No result");
                    }else{
                        JSONObject element = data.getJSONObject(0);
                        if(element.getJSONObject("p1").getString("value") != null &&
                                element.getJSONObject("p2").getString("value") != null){
                            wikiS = 2.0f;
                        }else if(element.getJSONObject("p1").getString("value") != null ||
                                element.getJSONObject("p2").getString("value") != null){
                            wikiS = 1.0f;
                        }
                    }
                    //TODO: wikiS
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
        AppController.getInstance().addToRequestQueue(stringRequest, "sim");
    }

    private void abstractS(String uri1, String uri2){
        String url = Utils.createUrlAbstractS(uri1, uri2);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                float abtractS = 0.0f;
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray data = json.getJSONObject("results").getJSONArray("bindings");
                    if(data.length() == 0){
                        showLog("ResponseAbtractS: No result");
                    }else{
                        JSONObject element = data.getJSONObject(0);
                        String label1 = element.getJSONObject("label1").getString("value");
                        String abtract1 = element.getJSONObject("abtract1").getString("value");
                        String label2 = element.getJSONObject("label2").getString("value");
                        String abtract2 = element.getJSONObject("abtract2").getString("value");
                        abtractS = calWordContained(label1, abtract2) + calWordContained(label2, abtract1);
                    }
                    //TODO: abtractS
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
        AppController.getInstance().addToRequestQueue(stringRequest, "sim");
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
        List<String> contexts = getContext();
        for(String r: contexts){
            s += similarity(uri, r);
            if(s >= THRESHOLD){
                return true;
            }
        }
        return false;
    }

    private List<String> getContext(){
        List<String> contexts = new ArrayList<>();
        contexts.add("http://dbpedia.org/resource/PHP");
        contexts.add("http://dbpedia.org/resource/Java_(Programming_language)");
        contexts.add("http://dbpedia.org/resource/MySQL");
        contexts.add("http://dbpedia.org/resource/Oracle_Database");
        contexts.add("http://dbpedia.org/resource/Lisp_(Programming_language)");
        contexts.add("http://dbpedia.org/resource/C_Sharp_(Programming_language)");
        contexts.add("http://dbpedia.org/resource/SQLite");
        return contexts;
    }

    /*============================================================================================*/

    private void showLog(String message){
        Log.d(TAG, "Message: " + message);
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
