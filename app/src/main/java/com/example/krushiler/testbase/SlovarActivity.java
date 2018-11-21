package com.example.krushiler.testbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SlovarActivity extends AppCompatActivity {
    Intent intent;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    String[] s;
    Bundle extras;
    String string = "", extrasString;
    ArrayList<HashMap<String, String>> myArrList;
    ListView lv;
    HashMap<String, String> map;
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open(extrasString);
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
        setContentView(R.layout.activity_slovar);
        myArrList = new ArrayList<HashMap<String, String>>();
        SimpleAdapter adapter;
        map = new HashMap<String, String>();

        lv = (ListView) findViewById(R.id.listViewSlovar);
        extras = getIntent().getExtras();
        extrasString = extras.getString("name");
        intent = getIntent();
        extrasString = intent.getStringExtra("name");
        try {
            jsonObject = new JSONObject(loadJSONFromAsset());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonArray = jsonObject.getJSONArray("array");
            s = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                s[i] = jsonArray.get(i).toString();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter = new SimpleAdapter(
                this,
                myArrList,
                R.layout.list_item_slovar,
                new String[]{"q", "a"},
                new int[]{R.id.text1, R.id.text2});
        for (int i = 0; i < s.length; i+=2) {
            map.put("q", s[i]);
            map.put("a", "- " + s[i + 1]);
            myArrList.add(map);
        }
        lv.setAdapter(adapter);
    }
}
