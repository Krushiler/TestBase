package com.example.krushiler.testbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameActivity extends AppCompatActivity {
    int  o = 0, sch = 0, otv;
    List<String> whatRandom = new ArrayList();
    Random rand, rand1, rand2;
    int b1, b2, b3;
    boolean b = false;
    int n, rv;
    boolean clicked = false;
    Button bo1, bo2, bo3;
    TextView voprTV, failsTV;
    private static final Object sMonitor = new Object();
    JSONObject jsonObject;
    JSONArray jsonArray;
    String[] s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        try {
        for (int i = 0; i < 10; i++) {
            jsonObject.put("arr", "v"+i+1);
            jsonObject.put("arr", "a"+i+1);
        }
        jsonArray = jsonObject.getJSONArray("arr");
        Log.d("JsonSIZE", Integer.toString(jsonArray.length()));
        s=new String[jsonArray.length()];
        Log.d("RandomSIZE", Integer.toString(s.length));
        for (int i = 0; i < jsonArray.length(); i++) {
                s[i] = jsonArray.get(i).toString();
        }
        for (int i = 0; i < s.length/2; i ++){
            whatRandom.add(s[i*2]);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bo1 = (Button) findViewById(R.id.button1a);
        bo2 = (Button) findViewById(R.id.button2a);
        bo3 = (Button) findViewById(R.id.button3a);
        voprTV = (TextView) findViewById(R.id.tvquestion);
        failsTV = (TextView) findViewById(R.id.fails);
        writeText();
    }
public void writeText(){
        if (sch == 9){
        voprTV.setText("END");
        }else {
        failsTV.setText("Ошибки: " + o);
        }
        rand = new Random();
        b = false;
        rand1 = new Random();
        Log.d("RandomSIZE", Integer.toString(whatRandom.size()));
        rv = rand1.nextInt(whatRandom.size());

        whatRandom.remove(rv);

        voprTV.setText(s[rv*2]);
        otv = rand.nextInt(3)+1;
        setTextOnButtons();
        }
public void setTextOnButtons() {
        rand2 = new Random();
        if (otv == 1) {
        b1=rv * 2 + 1;
        do {
        rand2 = new Random();
        b2 = rand2.nextInt(s.length-1) / 2 / 2 * 2 + 1;
        } while (b2 == b1);
        do {
        rand2 = new Random();
        b3 = rand2.nextInt(s.length-1) / 2 / 2 * 2 + 1;
        } while (b3 == b1 || b3 == b2);
        } else if (otv == 2) {
        b2=rv * 2 + 1;
        do {
        rand2 = new Random();
        b1 = rand2.nextInt(s.length-1) / 2 / 2 * 2 + 1;
        } while (b1 == b2);
        do {
        rand2 = new Random();
        b3 = rand2.nextInt(s.length-1) / 2 / 2 * 2 + 1;
        } while (b3 == b2 || b3 == b1);
        } else {
        b3=rv * 2 + 1;
        do {
        rand2 = new Random();
        b1 = rand2.nextInt(s.length-1) / 2 / 2 * 2 + 1;
        } while (b1 == b3);
        do {
        rand2 = new Random();
        b2 = rand2.nextInt(s.length-1) / 2 / 2 * 2 + 1;
        } while (b2 == b1 || b2 == b3);
        }
        bo1.setText(s[b1]);
        bo2.setText(s[b2]);
        bo3.setText(s[b3]);
        }
public void check(){
        if (n != otv)
        {
        o++;
        failsTV.setText("Ошибки: " + o);
        b = false;
        }else{
        sch++;
        bo1.setEnabled(true);
        bo2.setEnabled(true);
        bo3.setEnabled(true);
        if (sch<10) {
        writeText();
        }else{
        Intent i = new Intent(this, WinActivity.class);
        i.putExtra("mistakes", o);
        startActivity(i);
        finish();
        }
        }
        }
public void onClicl1O(View v){
        n = 1;
        bo1.setEnabled(false);
synchronized (sMonitor) {
        sMonitor.notify();
        }
        check();
        }
public void onClicl2O(View v){
        n = 2;
        bo2.setEnabled(false);
synchronized (sMonitor) {
        sMonitor.notify();
        }
        check();
        }
public void onClicl3O(View v){
        n = 3;
        bo3.setEnabled(false);
synchronized (sMonitor) {
        sMonitor.notify();
        }
        check();
        }
        }
