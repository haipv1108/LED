package com.brine.hai.led.DBpediaRanker.Model;


/**
 * Created by phamhai on 15/01/2017.
 */

public class SLDResult {
    private String uri;
    private String label;
    private String abtract;

    public SLDResult(String uri, String label, String abtract){
        this.uri = uri;
        this.label = label;
        this.abtract = abtract;
    }

    public String getUri(){
        return uri;
    }

    public void setUri(String uri){
        this.uri = uri;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public String getAbtract(){
        return abtract;
    }

    public void setAbtract(String abtract){
        this.abtract = abtract;
    }
}
