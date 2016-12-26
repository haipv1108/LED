package com.brine.hai.led.DBpediaRanker;

/**
 * Created by hai on 23/12/2016.
 */

public class NodeS {
    private String uri;
    private int hits;
    private boolean ranked;
    private boolean in_context;

    public NodeS(String uri, int hits, boolean ranked){
        this.uri = uri;
        this.hits = hits;
        this.ranked = ranked;
    }

    public NodeS(String uri, int hits, boolean ranked, boolean in_context){
        this.uri = uri;
        this.hits = hits;
        this.ranked = ranked;
        this.in_context = in_context;
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

    public boolean isIn_context(){
        return in_context;
    }

    public void setIn_context(boolean in_context){
        this.in_context = in_context;
    }

    public boolean equals(NodeS obj) {
        if(this.uri.equals(obj.getUri()) &&
                this.hits == obj.getHits() &&
                this.ranked == obj.isRanked() &&
                this.in_context == obj.isIn_context())
            return true;
        return false;
    }
}
