package com.brine.hai.led.Model;

/**
 * Created by phamhai on 14/01/2017.
 */

public class FSResult {
    private String uri;
    private String label;
    private String description;
    private double score;
    private double rank;

    public FSResult(String uri, String label, String description, double score, double rank){
        this.uri = uri;
        this.label = label;
        this.description = description;
        this.score = score;
        this.rank = rank;
    }

    public String getLabel(){
        return label;
    }

    public String getUri(){
        return uri;
    }

    public String getDescription(){
        return description;
    }

    public double getScore(){
        return score;
    }

    public double getRank(){
        return rank;
    }
}

