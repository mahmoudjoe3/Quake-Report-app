package com.example.android.quakereport;

import android.util.Log;

import com.example.android.quakereport.Model.item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private static final String TAG ="QueryUtils" ;

    private QueryUtils() {
    }

    /**
     * Return a list of {@link item} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<item> extractEarthquakes(String JSON_RESPONSE) {
        ArrayList<item> earthquakes = new ArrayList<>();

        if(JSON_RESPONSE!=null) {
            try {
                JSONObject root = new JSONObject(JSON_RESPONSE);
                JSONArray earthQ_Arr = root.optJSONArray("features");
                for (int i = 0; i < earthQ_Arr.length(); i++) {
                    JSONObject EQ = earthQ_Arr.optJSONObject(i);
                    JSONObject prop = EQ.optJSONObject("properties");

                    float mag = (float) prop.optDouble("mag");
                    DecimalFormat format = new DecimalFormat("0.0");
                    mag = Float.parseFloat(format.format(mag));

                    String loc = prop.optString("place");

                    String mileSec = prop.optString("time");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                    String date = dateFormat.format(new Date(Long.parseLong(mileSec)));
                    String time = timeFormat.format(new Date(Long.parseLong(mileSec)));

                    String url = prop.optString("url");
                    Log.d(TAG, "extractEarthquakes: url " + i + " -> " + url);
                    item item = new item(mag, loc, date, time, url);
                    earthquakes.add(item);
                }

            } catch (JSONException e) {
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            }
        }
        // Return the list of earthquakes
        return earthquakes;

    }

}
