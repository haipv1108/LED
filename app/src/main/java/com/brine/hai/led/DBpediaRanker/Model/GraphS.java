package com.brine.hai.led.DBpediaRanker.Model;

import com.brine.hai.led.DBpediaRanker.NodeS;

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
}
