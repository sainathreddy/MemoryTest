package com.example.sainath.memorytest.utils;

/**
 * Created by sainath on 10/17/2015.
 */
public class Constants {
    public static final String TAG = "MemoryTest";

    // Game States
    public static final int GAME_STATE_LEARN = 0;
    public static final int GAME_STATE_IDENTIFY = 1;

    // Timer value constants
    public static final int SEC_IN_MILLIS = 1000;
    public static final int TIME_IN_MILLIS = 15 * SEC_IN_MILLIS;

    // Number of Images
    public static final int NUM_IMAGES = 9;

    // URL to fetch the images.
    public static final String REQUEST_URL_1 = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&tags=birds";
    public static final String REQUEST_URL_2 = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&tags=cats";
    public static final String REQUEST_URL_3 = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&tags=cars";

    //Static strings for the KeyNames to store in Bundle.
    public static final String COUNT_FOUND = "imagesFound";
    public static final String COUNT_TRIES = "triesCnt";
    public static final String GRID_DATA = "gridData";
    public static final String GAME_STATE = "gameState";
}
