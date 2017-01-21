package com.brine.hai.led.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamhai on 15/01/2017.
 */

public class DbpediaMusicConstant {
    private static final List<String> sMusicProperties = new ArrayList<>();
    private static final List<String> sMusicOntologies = new ArrayList<>();
    static {
        sMusicProperties.add("artist");
        sMusicProperties.add("lyricist");
        sMusicProperties.add("recorded");
        sMusicProperties.add("album");
        sMusicProperties.add("bSide");
        sMusicProperties.add("certification");
        sMusicProperties.add("composer");
        sMusicProperties.add("description");
        sMusicProperties.add("fromAlbum");
        sMusicProperties.add("genre");
        sMusicProperties.add("lastSingle");
        sMusicProperties.add("nextSingle");
        sMusicProperties.add("prev");
        sMusicProperties.add("producer");
        sMusicProperties.add("released");
        sMusicProperties.add("title");
        sMusicProperties.add("type");
        sMusicProperties.add("writer");
        sMusicProperties.add("name");
        sMusicProperties.add("song");
        sMusicProperties.add("trackNo");
        sMusicProperties.add("cover");
        sMusicProperties.add("birthPlace");
        sMusicProperties.add("alias");
        sMusicProperties.add("background");
        sMusicProperties.add("birthDate");
        sMusicProperties.add("website");
        sMusicProperties.add("yearsActive");
        sMusicProperties.add("derivatives");
        sMusicProperties.add("instruments");
        sMusicProperties.add("stylisticOrigins");
        sMusicProperties.add("deathDate");
        sMusicProperties.add("dateOfBirth");
        sMusicProperties.add("dateOfDeath");
        sMusicProperties.add("shortDescription");
        sMusicProperties.add("alternativeNames");
        sMusicProperties.add("placeOfBirth");
        sMusicProperties.add("placeOfDeath");
    }

    static {
        sMusicOntologies.add("thumbnail");
        sMusicOntologies.add("nextLink");
        sMusicOntologies.add("abstract");
        sMusicOntologies.add("album");
        sMusicOntologies.add("artist");
        sMusicOntologies.add("composer");
        sMusicOntologies.add("genre");
        sMusicOntologies.add("language");
        sMusicOntologies.add("lyrics");
        sMusicOntologies.add("previousWork");
        sMusicOntologies.add("producer");
        sMusicOntologies.add("recordDate");
        sMusicOntologies.add("recordLabel");
        sMusicOntologies.add("recordedIn");
        sMusicOntologies.add("releaseDate");
        sMusicOntologies.add("soundRecording");
        sMusicOntologies.add("trackNumber");
        sMusicOntologies.add("type");
        sMusicOntologies.add("imdbId");
        sMusicOntologies.add("nextAlbum");
        sMusicOntologies.add("deathPlace");
        sMusicOntologies.add("birthPlace");
    }

    public static List<String> getMusicProperties(){
        return sMusicProperties;
    }

    public static List<String> getMusicOntology(){
        return sMusicOntologies;
    }
}
