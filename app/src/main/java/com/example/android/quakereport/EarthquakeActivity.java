/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quakereport.Model.item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<item>> {
    private static final int EARTHQUAKE_LOADER_ID = 1;
    int MinMag = 1;
    int limit = 20;
    String orderBy = "time";
    String urlStr;
    item_adapter adapter;
    EarthQuakeLoader loader;
    public static final String TAG = "EarthquakeActivity.me";
    @BindView(R.id.list)
    RecyclerView earthquakeListView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.progress)
    ProgressBar progress;

    private String getURL() {
        return "https://earthquake.usgs.gov/fdsnws/event/1/" +
                "query?format=geojson&" +
                "minmagnitude=" + MinMag + "&" +
                "limit=" + limit + "&"
                + "orderby=" + orderBy;
    }

    private Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null &&
                    (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        ButterKnife.bind(this);
        Log.d(TAG, "LifeCycle onCreate: ");
        emptyView.setText(R.string.no_earthquakes_found);
        Log.d(TAG, "onCreate: isnet-->"+isNetworkAvailable(this.getApplication()));
        if(isNetworkAvailable(this.getApplication())) {
            adapter = new item_adapter(EarthquakeActivity.this);
            urlStr = getURL();

            earthquakeListView.setAdapter(adapter);
            earthquakeListView.setLayoutManager(new LinearLayoutManager(EarthquakeActivity.this));

            LoaderManager.getInstance(this).initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
        else {
            progress.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.no_internet_connection);
        }

    }

    private List<item> getEarthquakeListView() {
        Log.d(TAG, "LifeCycle loadInBackground: ");
        String JSONString = null;
        try {
            JSONString = HttpHandler.makeHttpRequest(create(urlStr));
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


    @NonNull
    @Override
    public Loader<List<item>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "LifeCycle onCreateLoader: ");
        loader = new EarthQuakeLoader(EarthquakeActivity.this, urlStr);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<item>> loader, List<item> data) {
        Log.d(TAG, "LifeCycle onLoadFinished: ");
        progress.setVisibility(View.GONE);
        if(data==null||data.isEmpty())
        {
            emptyView.setVisibility(View.VISIBLE);
        }else emptyView.setVisibility(View.GONE);

        adapter.setList(data);
        adapter.setOnClickListener(new item_adapter.OnClickListener() {
            @Override
            public void onClick(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<item>> loader) {
        Log.d(TAG, "LifeCycle onLoaderReset: ");
    }



    @OnClick(R.id.filterBtn)
    public void onViewClicked() {
        createDialog().show();
    }

    private AlertDialog createDialog() {

        final View view = getLayoutInflater().inflate(R.layout.filterdialog, null);
        final EditText minMag, rows;

        minMag = view.findViewById(R.id.minMag);
        minMag.setText(MinMag + "");
        rows = view.findViewById(R.id.rows);
        rows.setText(limit + "");
        final Spinner order = view.findViewById(R.id.order);

        return new AlertDialog.Builder(EarthquakeActivity.this)
                .setView(view).setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MinMag = Integer.parseInt(minMag.getText().toString());
                        limit = Integer.parseInt(rows.getText().toString());

                        orderBy = order.getSelectedItem().toString();
                        urlStr = getURL();

                        progress.setVisibility(View.VISIBLE);
                        LoaderManager.getInstance(EarthquakeActivity.this)
                                .restartLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeActivity.this);
                    }
                }).setNegativeButton("Back", null).create();

    }
}
