package com.example.krushiler.testbase;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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
import java.util.List;

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
    ImageButton searchBTN;

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
        et = (EditText) findViewById(R.id.findET);
        searchBTN = (ImageButton) findViewById(R.id.searchbtn);
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
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

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setAdapter(et.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setAdapter(String so){
        myArrList = new ArrayList<HashMap<String, String>>();
        SimpleAdapter adapter;
        List<Integer> teml = new ArrayList<Integer>();
        adapter = new SimpleAdapter(
                this,
                myArrList,
                R.layout.list_item_slovar,
                new String[]{"q", "a"},
                new int[]{R.id.text1, R.id.text2});
        for (int i = 0; i < s.length; i+=2) {
            if (s[i].toUpperCase().contains(so.toUpperCase())) {
                HashMap<String, String> map;
                map = new HashMap<String, String>();
                map.put("q", s[i]);
                map.put("a", s[i+1]);
                myArrList.add(map);
                teml.add(i+1);
            }
        }
        for (int i = 1; i < s.length; i+=2) {
            boolean valid = true;
            for (int j = 0; j < teml.size(); j++){
                if (i == teml.get(j)){
                    valid = false;
                }
            }
            if (s[i].toUpperCase().contains(so.toUpperCase()) && valid) {
                    HashMap<String, String> map;
                    map = new HashMap<String, String>();
                    map.put("q", s[i-1]);
                    map.put("a", s[i]);
                    myArrList.add(map);
            }
        }
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
    }

    public void onClickSearchbtn(View v){
        if (et.isFocused()){
            et.clearFocus();
            closeKeyboard();
        }else{
            et.requestFocus();
            showKeyboard();
        }
    }

    public void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    public void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

}
