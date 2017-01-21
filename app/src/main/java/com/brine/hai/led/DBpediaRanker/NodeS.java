package com.brine.hai.led.DBpediaRanker;

/**
 * Created by hai on 23/12/2016.
 */

public class NodeS {
    private String uri;
    private int hits;
    private boolean ranked;
    private boolean inContext;

    public NodeS(String uri, int hits, boolean ranked){
        this.uri = uri;
        this.hits = hits;
        this.ranked = ranked;
    }

    public NodeS(String uri, int hits, boolean ranked, boolean inContext){
        this.uri = uri;
        this.hits = hits;
        this.ranked = ranked;
        this.inContext = inContext;
    }

    public String getUri(){
        return uri;
    }


    public void setUri(String uri){
        this.uri = uri;
    }

    public int getHits(){
        return hits;
    }

    public void setHits(int hits){
        this.hits = hits;
    }

    public boolean isRanked(){
        return ranked;
    }

    public void setRanked(boolean ranked){
        this.ranked = ranked;
    }

    public boolean isInContext(){
        return inContext;
    }

    public void setInContext(boolean inContext){
        this.inContext = inContext;
    }

    public boolean equals(NodeS obj) {
        if(this.uri.equals(obj.getUri()) &&
                this.hits == obj.getHits() &&
                this.ranked == obj.isRanked() &&
                this.inContext == obj.isInContext())
            return true;
        return false;
    }
}
