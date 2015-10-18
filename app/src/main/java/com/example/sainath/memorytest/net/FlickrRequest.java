package com.example.sainath.memorytest.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.sainath.memorytest.R;
import com.example.sainath.memorytest.model.GridItem;
import com.example.sainath.memorytest.model.GridViewAdapter;
import com.example.sainath.memorytest.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sainath on 10/17/2015.
 */
public class FlickrRequest {
    public static Context mCtx = null;
    private static List<String> imgURLs = new ArrayList<>(Constants.NUM_IMAGES);
    private ArrayList<GridItem> gridData;
    private GridViewAdapter gridViewAdapter;

    public FlickrRequest(Context ctx, ArrayList<GridItem> data, GridViewAdapter gridAdap) {
        mCtx = ctx;
        gridData = data;
        gridViewAdapter = gridAdap;
        imgURLs.clear();
    }

    public void fetchImages(int urlNumber) {
        String url = Constants.REQUEST_URL_1;
        switch(urlNumber) {
            case 0: url = Constants.REQUEST_URL_1; break;
            case 1: url = Constants.REQUEST_URL_2; break;
            case 2: url = Constants.REQUEST_URL_3; break;
        }
        new FetchImagesJSONTask().execute(url);
    }

    public class FetchImagesJSONTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog mDialog = null;
        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(mCtx);
            mDialog.setIndeterminate(true);
            mDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            int result = 0;

            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = bufferedReader.readLine()) != null ; ) {
                        builder.append(line).append("\n");
                    }

                    if(null != inputStream){
                        inputStream.close();
                    }

                    parseResult(builder.toString());
                    result = 1;
                }else{
                    result = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
            if(result == 1) {
                gridViewAdapter.setGridData(gridData);
            }else {
                Toast.makeText(mCtx, R.string.data_fetch_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Utility method to parse the response string and construct the JSONObject.
     * From the JSONObject we extract the field corresponding to the URL of the image.
     * @param result The result from the query to Flickr API.
     */
    private void parseResult(String result) {
        final String items = "items";
        final String media = "media";
        final String imgMedia = "m";
        String imgURL = null;
        GridItem gridItem;
        try{
            //Remove the starting "jsonFlickrFeed(" and the trailing ")" to construct the JSONObject
            result = result.substring(15, result.length()-1);
            JSONObject response = new JSONObject(result);

            // Construct the JSONArray for the list of 'items'.
            JSONArray itemsArray = response.optJSONArray(items);

            // Iterate through the JSONArray and fetch the object denoted by the key 'media'.
            // The value corresponding to the key 'm' is the required Image URL.
            for(int i=0; i< itemsArray.length();i++ ){
                JSONObject item = itemsArray.optJSONObject(i);
                JSONObject mediaObj = item.optJSONObject(media);
                imgURL = mediaObj.optString(imgMedia);

                // We are trying to construct the URL which poinst to the thumbnail of size 150x150.
                // For more details, please visit "https://www.flickr.com/services/api/misc.urls.html".
                imgURL = imgURL.replace("_m.","_q.");
                Log.d(Constants.TAG, "Image URL is : " + imgURL);

                // Add the URLs to the imgURLs list and also the gridItem objects.
                // We are interested in only 9 images.
                if(imgURLs.size() < Constants.NUM_IMAGES) {
                    imgURLs.add(imgURL);
                    gridItem = new GridItem();
                    gridItem.setIsShown(true);
                    gridItem.setTitle("Image " + (i + 1));
                    gridItem.setImage(imgURL);
                } else
                    break;

                gridData.add(gridItem);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static List<String> getImageURLs() {
        return imgURLs;
    }
}
