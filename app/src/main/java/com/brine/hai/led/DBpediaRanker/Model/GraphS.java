package com.brine.hai.led.DBpediaRanker.Model;

/**
 * Created by hai on 25/12/2016.
 */

public class GraphS {
    private String root;
    private String uri;
    private float sim;

    public GraphS(String root, String uri, float sim){
        this.root = root;
        this.uri = uri;
        this.sim = sim;
    }

    private String getRoot(){
        return root;
    }

    private void setRoot(String root){
        this.root = root;
    }

    private String getUri(){
        return uri;
    }

    private void setUri(String uri){
        this.uri = uri;
    }

    private float getSim(){
        return sim;
    }

    private void setSim(float sim){
        this.sim = sim;
    }
}
