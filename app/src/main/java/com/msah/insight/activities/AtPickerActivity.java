package com.msah.insight.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.msah.insight.R;
import com.msah.insight.adapters.AtListAdapter;
import com.msah.insight.models.AtItem;
import com.msah.insight.styles.At;

import java.util.ArrayList;
import java.util.Random;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class AtPickerActivity extends AppCompatActivity {

    private ListView mListView;

    private ArrayList<AtItem> mAtItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_picker);
        this.mListView = this.findViewById(R.id.view_at_listview);
        setTitle("@");
        setupListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prepareData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupListeners() {
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AtItem atItem = mAtItems.get(position);
                Intent data = new Intent();
                data.putExtra(At.EXTRA_TAG, atItem);
                AtPickerActivity.this.setResult(Activity.RESULT_OK, data);
                AtPickerActivity.this.finish();
            }
        });
    }

    private void prepareData() {
        new DataLoadTask().executeOnExecutor(THREAD_POOL_EXECUTOR, "");
    }

    private void showData(ArrayList<AtItem> itemsList) {
        if (this.mListView.getAdapter() == null) {
            AtListAdapter listAdapter = new AtListAdapter(this, itemsList);
            this.mListView.setAdapter(listAdapter);
        } else {
            AtListAdapter listAdapter  = (AtListAdapter) this.mListView.getAdapter();
            listAdapter.setData(itemsList);
            listAdapter.notifyDataSetChanged();
        }
        this.mAtItems = itemsList;
    }

    private ArrayList<AtItem> makeDummyData() {
        ArrayList<AtItem> itemsList = new ArrayList<AtItem>();
        int[] iconIds = {
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
                R.drawable.at,
        };

        String[] names = {
                "Adale Lee",
                "Bill Gates",
                "Country Side",
                "Dummy Name",
                "Emily John",
                "Family Mart",
                "Glide Ant",
                "Gradle Maven",
                "Michael Jordan",
                "Steve Jobs",
        };

        for (int i = 0; i < 20; i++) {
            int index = new Random().nextInt(10);
            if (index > 9) index = 9;
            if (index < 0) index = 0;
            AtItem atItem = new AtItem(String.valueOf(iconIds[index]), names[index]);
            itemsList.add(atItem);
        }
        return itemsList;
    }

    private class DataLoadTask extends AsyncTask<String, String, ArrayList<AtItem>> {
        @Override
        protected ArrayList<AtItem> doInBackground(String... strings) {
            ArrayList<AtItem> dummyList = makeDummyData();
            return dummyList;
        }

        @Override
        protected void onPostExecute(ArrayList<AtItem> itemsList) {
            super.onPostExecute(itemsList);
            showData(itemsList);
        }
    }

}

