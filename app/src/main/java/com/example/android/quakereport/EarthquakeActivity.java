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

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.android.quakereport.Model.item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<item>> {
    private static final int EARTHQUAKE_LOADER_ID = 1;
    int MinMag = 1;
    int limit = 2000;
    String orderBy = "time";
    String urlStr;
    item_adapter adapter;
    ProgressBar progress;
    FloatingActionButton actionButton;
    EarthQuakeLoader loader;
    public static final String TAG = "EarthquakeActivity.me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        progress = findViewById(R.id.progress);
        RecyclerView earthquakeListView = findViewById(R.id.list);
        actionButton = findViewById(R.id.filterBtn);

        adapter = new item_adapter(EarthquakeActivity.this);
        urlStr = getURL();

        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setLayoutManager(new LinearLayoutManager(EarthquakeActivity.this));

        LoaderManager.getInstance(this).initLoader(EARTHQUAKE_LOADER_ID, null, this);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog().show();
            }
        });
    }


    @NonNull
    @Override
    public Loader<List<item>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        loader = new EarthQuakeLoader(EarthquakeActivity.this, urlStr);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<item>> loader, List<item> data) {
        Log.d(TAG, "onLoadFinished: ");
        progress.setVisibility(View.GONE);
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
        Log.d(TAG, "onLoaderReset: ");
    }

    private String getURL() {
        return "https://earthquake.usgs.gov/fdsnws/event/1/" +
                "query?format=geojson&" +
                "minmagnitude=" + MinMag + "&" +
                "limit=" + limit + "&"
                + "orderby=" + orderBy;
    }

    private AlertDialog createDialog() {

        final View view = getLayoutInflater().inflate(R.layout.filterdialog, null);
        final EditText minMag,rows;

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

                        loader.setUrl(urlStr);
                        loader.onStartLoading();

                    }
                }).setNegativeButton("Back", null).create();

    }

    
}
