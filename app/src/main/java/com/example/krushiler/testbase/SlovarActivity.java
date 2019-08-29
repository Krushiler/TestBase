package com.example.krushiler.testbase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    EditText et;
    Toolbar toolbar;

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
            HashMap<String, String> map;
            map = new HashMap<String, String>();
            map.put("q", s[i]);
            map.put("a", s[i + 1]);
            myArrList.add(map);
        }
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        et = (EditText) findViewById(R.id.findET);
        et.setEnabled(false);
        et.setEnabled(true);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sa, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable sa) {
                int index = lv.getFirstVisiblePosition();
                View v = lv.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());
                boolean b = true;
                String etText = et.getText().toString();
                for (int i = 0; i < s.length; i += 2){
                    b = true;
                    if (etText.length()<=s[i].charAt(i)) {
                        for (int j = 0; j < etText.length(); j++) {
                            if (Character.toLowerCase(etText.charAt(j)) == Character.toLowerCase(s[i].charAt(j))) {
                                continue;
                            } else {
                                b = false;
                                break;
                            }
                        }
                        if (b) {
                            lv.setSelectionFromTop(i, top);
                            Log.d("aaaa", s[i]);
                            break;
                        }
                    }
                }
            }
        });

    }
}
