package com.jnu.lbsprivacy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LoadGPXActivity extends AppCompatActivity {
    public static final int GET_GPX_FILENAME_REQUEST = 0;
    public static final int GET_GPX_FILENAME_RESULT = 0;
    public static final String GPX_FILENAME_PARAM = "GPX_FILENAME_PARAM";

    private ListView mListView = null;
    private TextView mTextView = null;
    private String[] gpxFileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_gpx_activity);

        mListView = (ListView)findViewById(R.id.gpx_list_view);
        getGPXFileList();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, gpxFileNames);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), "Load: " + value + ", you can show record route.", Toast.LENGTH_SHORT).show();
                getSelectedGPXAndReturn(value);

            }
        });


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getSelectedGPXAndReturn(String GPXFilename) {
        Intent intent = getIntent();
        intent.putExtra(GPX_FILENAME_PARAM, GPXFilename);
        setResult(GET_GPX_FILENAME_RESULT, intent);
        this.finish();
    }

    private void getGPXFileList() {
        gpxFileNames = Arrays.stream(getApplicationContext().fileList()).filter(s -> s.contains(".gpx")).toArray(String[]::new);
    }
}