package com.brine.hai.led.DBpediaRanker.Model;

/**
 * Created by hai on 23/12/2016.
 */

public class KeywordSearch {
    private String label;
    private String uri;

    public KeywordSearch(String label, String uri){
        this.label = label;
        this.uri = uri;
    }

    public String getLabel(){
        return label;
    }

    public String getUri(){
        return uri;
    }
}
