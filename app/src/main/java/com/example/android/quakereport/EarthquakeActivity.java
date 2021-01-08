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
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.quakereport.Model.item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EarthquakeActivity extends AppCompatActivity {
    FloatingActionButton actionButton;
    int MinMag=1;
    int sYear=2019;
    int eYear=2020;
    int limit=20000;
    String orderBy="time";
    String urlStr ;
    item_adapter adapter;
    ProgressBar progress;
    private String getURL() {
        
        return "https://earthquake.usgs.gov/fdsnws/event/1/"+
        "query?format=geojson&"+
        /*"starttime=" +sYear+"-01-01&"+
        "endtime="+eYear+"-01-01&"+
        "minmagnitude="+MinMag+"&"+*/
        "limit="+limit+"&"
        +"orderby="+orderBy;

    }

    public static final String TAG = EarthquakeActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        progress=findViewById(R.id.progress);
        RecyclerView earthquakeListView = findViewById(R.id.list);
        adapter = new item_adapter( EarthquakeActivity.this);
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setLayoutManager(new LinearLayoutManager(EarthquakeActivity.this));

        urlStr = getURL();
        new EarthQuakeAsyncTask().execute();

        /*
        getList().subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ArrayList<item>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progress.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onSuccess(ArrayList<item> items) {
                        progress.setVisibility(View.GONE);
                        adapter.setList(items);
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
                    public void onError(Throwable e) {

                    }
                });
*/
        actionButton=findViewById(R.id.filterBtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view= getLayoutInflater().inflate(R.layout.filterdialog,null);
                final EditText sY,eY,minMag;
                sY=view.findViewById(R.id.startYear);
                sY.setText(sYear+"");
                eY=view.findViewById(R.id.endYear);
                eY.setText(eYear+"");
                minMag=view.findViewById(R.id.minMag);
                minMag.setText(MinMag+"");
                final Spinner order=view.findViewById(R.id.order);

                AlertDialog.Builder builder=new AlertDialog.Builder(EarthquakeActivity.this);
                builder.setView(view).setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MinMag=Integer.parseInt(minMag.getText().toString());
                        sYear=Integer.parseInt(sY.getText().toString());

                        eYear=Integer.parseInt(eY.getText().toString());;
                        orderBy=order.getSelectedItem().toString();
                        urlStr=getURL();

                        new EarthQuakeAsyncTask().execute();
                    }
                }).setNegativeButton("Back",null).create().show();
            }
        });
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


    Single<ArrayList<item>> getList(){
        String JSONString = null;
        try {
            JSONString = HttpHandler.makeHttpRequest(create(urlStr));
        } catch (IOException e) {
            Log.e(TAG, "onCreate: makeHttpRequest error->", e);
        }
        return Single.just(QueryUtils.extractEarthquakes(JSONString));
    }

    private class EarthQuakeAsyncTask extends AsyncTask<Void, Void, Void> {
        List<item> earthquakes2=new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String JSONString = null;
            try {
                JSONString = HttpHandler.makeHttpRequest(create(urlStr));
            } catch (IOException e) {
                Log.e(TAG, "onCreate: makeHttpRequest error->", e);
            }

            earthquakes2 = QueryUtils.extractEarthquakes(JSONString);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.setVisibility(View.GONE);
            adapter.setList(earthquakes2);
            adapter.setOnClickListener(new item_adapter.OnClickListener() {
                @Override
                public void onClick(String url) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
    }

}
