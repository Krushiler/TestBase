package com.example.krushiler.testbase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ChangeTestActivity extends AppCompatActivity {
    Intent intent;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    String[] s;
    String fileName;
    ArrayList<String> myArrList;
    ListView lv;
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("change.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_test);
        myArrList = new ArrayList<String>();
        ArrayAdapter adapter;


        lv = (ListView) findViewById(R.id.listViewTests);
        try {
            jsonObject = new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonArray = jsonObject.getJSONArray("array");
            s = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                s[i] = jsonArray.get(i).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String sm;
        adapter = new ArrayAdapter(this, R.layout.item_list_change, this.myArrList);
        BaseAdapter bs;
        for (int i = 0; i < s.length; i += 2) {
            HashMap<String, String> map;
            sm = s[i];
            myArrList.add(sm);
        }
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileName = s[position*2+1];
                onClickChangedGame();
            }
        });
    }
    public void onClickChangedGame(){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("name", fileName);
        startActivity(i);
    }
}