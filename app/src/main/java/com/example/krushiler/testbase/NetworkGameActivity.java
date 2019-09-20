package com.example.krushiler.testbase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NetworkGameActivity extends AppCompatActivity {
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };



    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    private String opponentEndpointId;
    private String opponentName;
    private String codeName = "name";
    private int opponentScore;
    private List<String> opponentEndpointIds = new ArrayList<>();

    private TextView opponentText;
    private TextView statusText;

    private ConnectionsClient connectionsClient;

    int  o = 0, sch = 0, otv; // sch - counter    o - marks   otv - right answer
    int COUNT_OF_QUESTIONS = 10;
    //final MediaPlayer rightAnswer = MediaPlayer.create(this, R.raw.rightanswersound);
    List<String> whatRandom = new ArrayList();
    List<String> allArray = new ArrayList();
    Random rand = new Random(), rand1  = new Random(), rand2  = new Random(), randseed = new Random();
    int b1, b2, b3;
    long seed=1;
    int n, rv;
    Bundle extras;
    Button bo1, bo2, bo3;
    TextView voprTV, failsTV, schTV, hostTV, searchTV, resultsTV, timeTV, countDownTV, whatTestTV;
    EditText nameET, countQestET;
    ProgressBar connectionPB;
    File jsonFile = new File(Environment.getDataDirectory(), "");
    Intent intent;
    Timer timer = new Timer();
    private static final Object sMonitor = new Object();
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    //
    String resultsString = new String();
    String[] s;
    int countTime = 0;
    String extrasString, extrasWho;
    Map<String, String> opponentNames = new HashMap<String, String>();
    Map<String, Integer> opponentScores = new HashMap<String, Integer>();
    Map<String, Integer> opponentTimes = new HashMap<String, Integer>();
    Map<String, Integer> opponentMessages = new HashMap<String, Integer>();

    JSONObject jsonObjectChange = new JSONObject();
    JSONArray jsonArrayChange = new JSONArray();
    String[] sChange;
    String fileName;
    ArrayList<String> myArrList;
    ListView lv;

    String TAG = "Nsd";

    Handler mUpdateHandler;

    String message;

    int connectedPlayers = 0;

    private Button mRegisterBtn;
    private Button mDiscoverBtn;
    private Button mSayHelloBtn;
    private Button restartBTN;

    String gameStage = "getNames";

    boolean finishedGame = false;

    Button startBTN, cancelBTN, stopGameBTN;

    int messageCount = 0;
    RelativeLayout changeRL, gameRL, networkRL, waitRL, hostRL, resultsRL, countDownRL, waitOthersStopRL;

    String fnameforclient;

    // Callbacks for receiving payloads
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    message = new String(payload.asBytes(), UTF_8);
                    Log.i(TAG, message);
                    if (extrasWho=="client") {
                        messageCount++;
                        if (messageCount == 1){
                            COUNT_OF_QUESTIONS = Integer.parseInt(message);
                        }
                        if (messageCount == 2) {
                            extrasString = message;
                            countTime = 0;
                            o = 0;
                        }
                        else if (messageCount == 3) {
                            fnameforclient = message;
                        }
                        else if (messageCount == 4) {
                            seed = Long.parseLong(message);
                            startGame();
                        } else if (messageCount == 5){
                            waitOhtersStop(true);
                            showLayout(resultsRL);
                            resultsTV.setText(message);
                            messageCount = 0;
                        }
                    }
                    if (extrasWho == "host"){
                        getResults();
                        if (gameStage == "getNames"){
                            opponentNames.put(endpointId, message);
                        }
                        if (gameStage == "getScore") {
                            int tempcount = opponentMessages.containsKey(endpointId) ? opponentMessages.get(endpointId) : 1;
                            opponentMessages.put(endpointId, tempcount+1);
                            if (opponentMessages.get(endpointId) == 2) {
                                opponentScores.put(endpointId, Integer.parseInt(message));
                            }else if (opponentMessages.get(endpointId)==3){
                                opponentTimes.put(endpointId, Integer.parseInt(message));
                                getResults();
                            }
                        }
                    }
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    }
                }
            };

    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    connectionsClient.requestConnection(codeName, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {}
            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    opponentName = connectionInfo.getEndpointName();
                }

                @Override
                
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        codeName = nameET.getText().toString();
                        nameET.setEnabled(false);
                        Log.i(TAG, "onConnectionResult: connection successful");
                        connectionsClient.stopDiscovery();
                        //connectionsClient.stopAdvertising();

                        connectedPlayers++;
                        opponentEndpointId = endpointId;
                        opponentEndpointIds.add(opponentEndpointId);

                        if (extrasWho == "host"){
                            hostTV.setText("Игроков подключено: " + connectedPlayers);
                        }
                        if(extrasWho == "client") {
                            jsonArray = new JSONArray();
                            jsonObject = new JSONObject();

                            try {
                                jsonObject = new JSONObject(loadJSONFromAssetChange());
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

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    fileName = s[position * 2 + 1];
                                    fnameforclient = s[position*2];
                                    onClickChangedGame();
                                    sendMessage(fileName);
                                    sendMessage(fnameforclient);
                                    startGame();
                                }
                            });
                            searchTV.setText("Подключено");
                            connectionPB.setVisibility(View.INVISIBLE);
                            mRegisterBtn.setEnabled(false);
                            mDiscoverBtn.setEnabled(false);
                            sendMessage(codeName);
                        }
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    nameET.setEnabled(true);
                    opponentEndpointIds.remove(endpointId);
                    opponentNames.remove(endpointId);
                    opponentScores.remove(endpointId);
                    opponentTimes.remove(endpointId);
                    connectedPlayers--;
                    if (extrasWho == "host"){
                        hostTV.setText("Игроков подключено: " + connectedPlayers);
                    }
                    if (extrasWho == "client"){
                        mRegisterBtn.setEnabled(true);
                        mDiscoverBtn.setEnabled(true);
                    }
                    if (opponentEndpointIds.size()==0){
                        resetAll();
                        timer = new Timer();
                        showLayout(networkRL);
                        try {
                            jsonObject = new JSONObject(loadJSONFromAssetChange());
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

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                fileName = s[position * 2 + 1];
                                fnameforclient = s[position*2];
                                onClickChangedGame();
                                sendMessage(fileName);
                                sendMessage(fnameforclient);
                                startGame();
                            }
                        });
                    }
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                    getResults();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        gameStage = "getNames";

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
        connectionsClient = Nearby.getConnectionsClient(this);

        changeRL = (RelativeLayout) findViewById(R.id.changelayout);
        gameRL = (RelativeLayout) findViewById(R.id.gamelayout);
        networkRL = (RelativeLayout) findViewById(R.id.networklayout);
        mRegisterBtn = (Button) findViewById(R.id.register);
        mDiscoverBtn = (Button) findViewById(R.id.discover);
        hostTV = (TextView) findViewById(R.id.hosttextview);
        searchTV = (TextView) findViewById(R.id.connectiontextview);
        hostTV = (TextView) findViewById(R.id.hosttextview);
        connectionPB = (ProgressBar) findViewById(R.id.connectionbar);
        startBTN = (Button) findViewById(R.id.startButton);
        hostRL = (RelativeLayout) findViewById(R.id.hostlayout);
        waitRL = (RelativeLayout) findViewById(R.id.waitingconnectionlayout);
        nameET = (EditText) findViewById(R.id.nameET);
        resultsRL = (RelativeLayout) findViewById(R.id.resultsLayout);
        resultsTV = (TextView) findViewById(R.id.resultsTV);
        timeTV = (TextView) findViewById(R.id.timeTV);
        restartBTN = (Button) findViewById(R.id.restartBTN);
        cancelBTN = (Button) findViewById(R.id.cancelButton);
        stopGameBTN = (Button) findViewById(R.id.stopGameBTN);
        countDownRL = (RelativeLayout) findViewById(R.id.countDownLayout);
        countDownTV = (TextView) findViewById(R.id.countDownTV);
        countQestET = (EditText) findViewById(R.id.countQuestET);
        whatTestTV = (TextView) findViewById(R.id.whatTestTV);
        waitOthersStopRL = (RelativeLayout) findViewById(R.id.waitotherplayersstoprl);

        nameET.setOnKeyListener(new View.OnKeyListener() {
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

        countQestET.setOnKeyListener(new View.OnKeyListener() {
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



        myArrList = new ArrayList<String>();
        ArrayAdapter adapter;

        lv = (ListView) findViewById(R.id.listViewTests);
        try {
            jsonObject = new JSONObject(loadJSONFromAssetChange());
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
                fileName = s[position * 2 + 1];
                fnameforclient = s[position*2];
                onClickChangedGame();
                sendMessage(fileName);
                sendMessage(fnameforclient);
                startGame();
            }
        });

        countQestET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                countQestET.setText("");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public String loadJSONFromAsset() {
        String json = new String();
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
    boolean b = false;

    public String loadJSONFromAssetChange() {
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

    public void initializeGame(){
        Log.d(TAG, Long.toString(seed));
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
        timer.purge();
        writeText();
    }
    public void writeText(){
        schTV.setText(Integer.toString(sch+1)+" вопрос");
        rand = new Random(); rand1 = new Random();rand2 = new Random();
        rand.setSeed(seed);
        rand1.setSeed(seed);
        rand2.setSeed(seed);
        if (sch == COUNT_OF_QUESTIONS){
            voprTV.setText("END");
        }else {
            failsTV.setText("Ошибки: " + o);
        }
        Log.i(TAG, "WOW");

        b = false;
        Log.d("RandomSIZE", Integer.toString(whatRandom.size()));
        rv = rand1.nextInt(whatRandom.size());

        voprTV.setText(allArray.get(rv*2+1));
        otv = rand.nextInt(3)+1;

        setTextOnButtons();
        whatRandom.remove(rv);
        allArray.remove(rv*2);
        allArray.remove(rv*2);
        Log.i(TAG, rv +" " + otv + " " + seed);
        seed ++;
    }
    public void setTextOnButtons() {

        if (otv == 1) {
            b1=rv*2;
            do {
                b2 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b2] == allArray.get(b1));
            do {
                b3 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b3] == allArray.get(b1) || b3 == b2);
            bo1.setText(allArray.get(b1));
            bo2.setText(s[b2]);
            bo3.setText(s[b3]);
        } else if (otv == 2) {
            b2=rv * 2;
            do {
                b1 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b1] == allArray.get(b2));
            do {
                b3 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b3] == allArray.get(b2) || b3 == b1);
            bo2.setText(allArray.get(b2));
            bo1.setText(s[b1]);
            bo3.setText(s[b3]);
        } else {
            b3 = rv * 2;
            do {
                b1 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b1] == allArray.get(b3));
            do {
                b2 = rand2.nextInt(s.length) / 2 / 2 * 2;
            } while (s[b2] == allArray.get(b3) || b2 == b1);
            bo3.setText(allArray.get(b3));
            bo1.setText(s[b1]);
            bo2.setText(s[b2]);
        }
    }

    public void waitOhtersStop(boolean b){
        restartBTN.setEnabled(b);
        stopGameBTN.setEnabled(b);
        if (b==true) {
            waitOthersStopRL.setVisibility(View.GONE);
        }else {
            waitOthersStopRL.setVisibility(View.VISIBLE);
        }
    }

    public void check(){
            if (n != otv) {
                o++;
                failsTV.setText("Ошибки: " + o);
                b = false;
            } else {
                // rightAnswer.start();
                sch++;
                bo1.setEnabled(true);
                bo2.setEnabled(true);
                bo3.setEnabled(true);
                if (sch < COUNT_OF_QUESTIONS) {
                    writeText();
                } else {
                    resultsTV.setText("Ошибки: " + o + " Время: " + countTime);
                    waitOhtersStop(false);
                    showLayout(resultsRL);
                    timer.cancel();
                    timer.purge();
                    finishedGame = true;
                    timer = new Timer();
                    if (extrasWho == "client") {
                        sendMessage(Integer.toString(o));
                        sendMessage(Integer.toString(countTime));
                    }
                    if (extrasWho == "host") {
                        getResults();
                        startAdvertising();
                    }
                    jsonArray = new JSONArray();
                    jsonObject = new JSONObject();

                    try {
                        jsonObject = new JSONObject(loadJSONFromAssetChange());
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

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            fileName = s[position * 2 + 1];
                            fnameforclient = s[position*2];
                            onClickChangedGame();
                            sendMessage(fileName);
                            sendMessage(fnameforclient);
                            startGame();
                        }
                    });
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

    public void onClickChangedGame(){
        extrasString = fileName;
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Handles user acceptance (or denial) of our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this,"Необходимы все разрешения", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }

    public void startGameByHost(){
        showLayout(changeRL);
        o = 0;
        countTime = 0;
        sch = 0;
        connectionsClient.stopAdvertising();

    }

    public void onClickRestart(View v){
        countQestET.setText(Integer.toString(COUNT_OF_QUESTIONS));
        showLayout(hostRL);
        startAdvertising();

    }

    public void startGame(){
        whatRandom = new ArrayList<>();
        allArray = new ArrayList<>();
        showLayout(countDownRL);
        if (extrasWho == "host") {
            randseed = new Random();
            extrasString = fileName;
            randseed = new Random();
            seed = randseed.nextLong();
            sendMessage(Long.toString(seed));
        }
        final int[] nowTime = {4};
        whatTestTV.setText(fnameforclient);
        countDownTV.setText("3");
        CountDownTimer waitTimer;
        waitTimer = new CountDownTimer(2500, 700) {

            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nowTime[0]--;
                        countDownTV.post(new Runnable() {
                            @Override
                            public void run() {
                                countDownTV.setText(Integer.toString(nowTime[0]));
                            }
                        });
                    }
                });
            }

            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startGameAfterTimer();
                            }
                        });
                    }
                });
            }
        }.start();
    }

    public void startGameAfterTimer(){
        finishedGame = false;
        gameStage = "getScore";
        resetMapsAndLists();
        resultsTV.setText("");
        o = 0;
        countTime = 0;
        sch = 0;
        initializeGame();
        nameET.setEnabled(true);
        showLayout(gameRL);
        mRegisterBtn.setEnabled(true);
        mDiscoverBtn.setEnabled(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countTime+=1;
                timeTV.post(new Runnable() {
                    @Override
                    public void run() {
                        timeTV.setText(Integer.toString(countTime+o*10));
                    }
                });
            }
        }, 0, 1000);
        /*CountDownTimer waitTimer;
        waitTimer = new CountDownTimer(5000, 300) {

            public void onTick(long millisUntilFinished) {
                //called every 300 milliseconds, which could be used to
                //send messages or some other action
            }

            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        o = 999;
                        countTime = 999;
                        n = otv;
                        sch = COUNT_OF_QUESTIONS+1;
                        finishedGame = true;

                    }
                });
                bo1.post(new Runnable() {
                    @Override
                    public void run() {
                        bo1.callOnClick();
                    }
                });
            }
        }.start();*/
    }

    public void onClickStartGame(View view){
        COUNT_OF_QUESTIONS = Integer.parseInt(countQestET.getText().toString());
        sendMessage(Integer.toString(COUNT_OF_QUESTIONS));
        startGameByHost();
    }


    /** Finds an opponent to play the game with using Nearby Connections. */
    public void findOpponent(View view) {
        startDiscovery();
        //startAdvertising();
        extrasWho = "client";
        restartBTN.setVisibility(View.GONE);
        waitRL.setVisibility(View.VISIBLE);

        connectionPB.setVisibility(View.VISIBLE);
        searchTV.setText("Поиск...");
    }

    public void hostGame(View view) {
        startAdvertising();
        countQestET.setText(Integer.toString(COUNT_OF_QUESTIONS));
        extrasWho = "host";
        restartBTN.setVisibility(View.VISIBLE);
        codeName = nameET.getText().toString();
        showLayout(hostRL);
    }

    /** Disconnects from the opponent and reset the UI. */
    public void disconnect(View view) {
        for (int i = 0; i < opponentEndpointIds.size(); i++) {
            connectionsClient.disconnectFromEndpoint(opponentEndpointIds.get(i));
        }
        showLayout(networkRL);
        connectionsClient.stopDiscovery();
        connectionsClient.stopAdvertising();
        mDiscoverBtn.setEnabled(true);
        mRegisterBtn.setEnabled(true);
        resetAll();
    }

    /** Starts looking for other players using Nearby Connections. */
    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.stopAdvertising();
        connectionsClient.stopDiscovery();


        connectionsClient.startDiscovery(
                getPackageName(), endpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(STRATEGY).build());
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.stopAdvertising();
        connectionsClient.stopDiscovery();

        connectionsClient.startAdvertising(
                codeName, getPackageName(), connectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build());
    }
    private void sendMessage(String me) {
        Log.d(TAG, "message sent: " + me);
        for (int i = 0; i < opponentEndpointIds.size(); i++) {
            connectionsClient.sendPayload(
                    opponentEndpointIds.get(i), Payload.fromBytes(me.getBytes(UTF_8)));
        }

    }

    @Override
    protected void onStop() {
        connectionsClient.stopAllEndpoints();

        super.onStop();
    }

    public void showLayout(RelativeLayout rl){
        hostRL.setVisibility(View.GONE);
        waitRL.setVisibility(View.GONE);
        gameRL.setVisibility(View.GONE);
        networkRL.setVisibility(View.GONE);
        changeRL.setVisibility(View.GONE);
        resultsRL.setVisibility(View.GONE);
        countDownRL.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
    }

    public void showLayout(LinearLayout rl){
        hostRL.setVisibility(View.GONE);
        waitRL.setVisibility(View.GONE);
        gameRL.setVisibility(View.GONE);
        networkRL.setVisibility(View.GONE);
        changeRL.setVisibility(View.GONE);
        resultsRL.setVisibility(View.GONE);
        countDownRL.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
    }

    public void resetMapsAndLists(){
        finishedGame = false;
        opponentMessages = new HashMap<String, Integer>();
        opponentNames.remove("me");
        opponentScores = new HashMap<String, Integer>();
        opponentTimes = new HashMap<String, Integer>();
    }

    public void resetAll(){
        finishedGame = false;
        opponentMessages = new HashMap<String, Integer>();
        opponentNames = new HashMap<String, String>();
        opponentScores = new HashMap<String, Integer>();
        opponentTimes = new HashMap<String, Integer>();
        opponentEndpointIds = new ArrayList<String>();
    }

    public void getResults(){
        if (opponentTimes.size()==opponentEndpointIds.size() && finishedGame == true){
            int hoho = 1;
            waitOhtersStop(true);
            List<String> tempEndpoints = new ArrayList<String>(opponentEndpointIds);
            tempEndpoints.add("me");
            opponentTimes.put("me", countTime);
            opponentScores.put("me", o);
            opponentNames.put("me", codeName);
            resultsString = "";
            while(tempEndpoints.size()>0){
                String bestEndpoint = new String(), nam = new String();
                int minMis=10000, minTime=10000, minScore=10000;
                for (int i = 0; i < tempEndpoints.size(); i++){
                    int mis = opponentScores.get(tempEndpoints.get(i)), tim = opponentTimes.get(tempEndpoints.get(i)), scor = tim + mis*10;
                    if (scor<minScore){
                        minMis = mis;
                        minTime = tim;
                        minScore = scor;
                        bestEndpoint = tempEndpoints.get(i);
                    }else if (scor==minScore){
                        if (mis <= minMis){
                            minMis = mis;
                            minTime = tim;
                            minScore = scor;
                            bestEndpoint = tempEndpoints.get(i);
                        }
                    }
                }

                resultsString += hoho + ": " + opponentNames.get(bestEndpoint) + " Ошибки: " + minMis + " Время: " + minTime + "\n";

                tempEndpoints.remove(bestEndpoint);
                hoho ++;
            }
            resultsTV.setText(resultsString);
            sendMessage(resultsTV.getText().toString());

        }
    }

    public static int findIndex(String arr[], String t)
    {

        // if array is Null
        if (arr == null) {
            return -1;
        }

        // find length of array
        int len = arr.length;
        int i = 0;

        // traverse in the array
        while (i < len) {

            // if the i-th element is t
            // then return the index
            if (arr[i] == t) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }
}
