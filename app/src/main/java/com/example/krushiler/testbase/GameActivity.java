package com.example.krushiler.testbase;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    int  o = 0, sch = 0, otv; // sch - counter    o - marks   otv - right answer
    final static int COUNT_OF_QUESTIONS = 15;
    //final MediaPlayer rightAnswer = MediaPlayer.create(this, R.raw.rightanswersound);
    List<String> whatRandom = new ArrayList();
    List<String> allArray = new ArrayList();
    Random rand = new Random(), rand1  = new Random(), rand2  = new Random(), randseed = new Random();
    int b1, b2, b3;
    long seed=1;
    boolean b = false;
    int n, rv;
    Bundle extras;
    Button bo1, bo2, bo3;
    TextView voprTV, failsTV, schTV, timeTV;
    File jsonFile = new File(Environment.getDataDirectory(), "");
    Intent intent;
    Timer timer = new Timer();
    private static final Object sMonitor = new Object();
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    String[] s;
    int countTime = 0;
    int countTimeSec;
    String extrasString;
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
        setContentView(R.layout.activity_game);
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

            Log.d("JsonSIZE", Integer.toString(jsonArray.length()));
            s=new String[jsonArray.length()];
            Log.d("RandomSIZE", Integer.toString(s.length));
            for (int i = 0; i < jsonArray.length(); i++) {
                s[i] = jsonArray.get(i).toString();
            }
            for (int i = 0; i < s.length/2; i ++){
                whatRandom.add(s[i*2+1]);
            }
            for (int i = 0; i < s.length; i ++){
                allArray.add(s[i]);
            }
            Log.d("AllSIZE", Integer.toString(allArray.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bo1 = (Button) findViewById(R.id.button1a);
        bo2 = (Button) findViewById(R.id.button2a);
        bo3 = (Button) findViewById(R.id.button3a);
        voprTV = (TextView) findViewById(R.id.tvquestion);
        failsTV = (TextView) findViewById(R.id.fails);
        schTV = (TextView) findViewById(R.id.tvsch);
        timeTV = (TextView) findViewById(R.id.time);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countTime+=1;
                timeTV.post(new Runnable() {
                    @Override
                    public void run() {
                        timeTV.setText(Integer.toString(countTime));
                    }
                });
            }
        }, 0, 1000);
        writeText();
    }
    public void writeText(){
        randseed = new Random();
        seed = randseed.nextLong();
        rand.setSeed(seed);
        rand1.setSeed(seed);
        rand2.setSeed(seed);
        schTV.setText(Integer.toString(sch+1)+" вопрос");
        if (sch == COUNT_OF_QUESTIONS){
            voprTV.setText("END");
        }else {
            failsTV.setText("Ошибки: " + o);
        }
        rand = new Random();
        b = false;
        rand1 = new Random();
        Log.d("RandomSIZE", Integer.toString(whatRandom.size()));
        rv = rand1.nextInt(whatRandom.size());

        voprTV.setText(allArray.get(rv*2+1));
        otv = rand.nextInt(3)+1;

        setTextOnButtons();
        whatRandom.remove(rv);
        allArray.remove(rv*2);
        allArray.remove(rv*2);
        seed ++;
    }
    public void setTextOnButtons() {
        rand2 = new Random();
        if (otv == 1) {
            b1=rv*2;
            do {
                rand2 = new Random();
                b2 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b2] == allArray.get(b1));
            do {
                rand2 = new Random();
                b3 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b3] == allArray.get(b1) || b3 == b2);
            bo1.setText(allArray.get(b1));
            bo2.setText(s[b2]);
            bo3.setText(s[b3]);
        } else if (otv == 2) {
            b2=rv * 2;
            do {
                rand2 = new Random();
                b1 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b1] == allArray.get(b2));
            do {
                rand2 = new Random();
                b3 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b3] == allArray.get(b2) || b3 == b1);
            bo2.setText(allArray.get(b2));
            bo1.setText(s[b1]);
            bo3.setText(s[b3]);
        } else {
            b3 = rv * 2;
            do {
                rand2 = new Random();
                b1 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b1] == allArray.get(b3));
            do {
                rand2 = new Random();
                b2 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b2] == allArray.get(b3) || b2 == b1);
            bo3.setText(allArray.get(b3));
            bo1.setText(s[b1]);
            bo2.setText(s[b2]);
        }
    }
    public void check(){
        if (n != otv)
        {
            o++;
            failsTV.setText("Ошибки: " + o);
            b = false;
        }else{
           // rightAnswer.start();
            sch++;
            bo1.setEnabled(true);
            bo2.setEnabled(true);
            bo3.setEnabled(true);
            if (sch<COUNT_OF_QUESTIONS) {
                writeText();
            }else{
                Intent i = new Intent(this, WinActivity.class);
                i.putExtra("mistakes", o);
                i.putExtra("time", Double.toString(countTime));
                i.putExtra("onlineMode", "offline");
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