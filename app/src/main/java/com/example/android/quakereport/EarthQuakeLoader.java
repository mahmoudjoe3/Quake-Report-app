package com.example.android.quakereport;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.android.quakereport.Model.item;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EarthQuakeLoader extends AsyncTaskLoader<List<item>> {
    private static final String TAG ="EarthquakeActivity" ;
    String url;
    public EarthQuakeLoader(@NonNull Context context, String url) {
        super(context);
        this.url=url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG, "LifeCycle onStartLoading: ");
        forceLoad();
    }

    @Nullable
    @Override
    public List<item> loadInBackground() {

        Log.d(TAG, "LifeCycle loadInBackground: ");
        String JSONString = null;
        try {
            JSONString = HttpHandler.makeHttpRequest(create(url));
        } catch (IOException e) {
            Log.e(TAG, "onCreate: makeHttpRequest error->", e);
        }
        return QueryUtils.extractEarthquakes(JSONString);
    }

    private URL create(String urlStr) {
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}