package com.example.krushiler.testbase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    private TextView opponentText;
    private TextView statusText;

    private ConnectionsClient connectionsClient;

    int  o = 0, sch = 0, otv; // sch - counter    o - marks   otv - right answer
    final static int COUNT_OF_QUESTIONS = 15;
    //final MediaPlayer rightAnswer = MediaPlayer.create(this, R.raw.rightanswersound);
    List<String> whatRandom = new ArrayList();
    List<String> allArray = new ArrayList();
    Random rand = new Random(), rand1  = new Random(), rand2  = new Random(), randseed = new Random();
    int b1, b2, b3;
    long seed=1;
    int n, rv;
    Bundle extras;
    Button bo1, bo2, bo3;
    TextView voprTV, failsTV, schTV;
    File jsonFile = new File(Environment.getDataDirectory(), "");
    Intent intent;
    Timer timer = new Timer();
    private static final Object sMonitor = new Object();
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    //
    String[] s;
    int countTime = 0;
    String extrasString, extrasWho;

    JSONObject jsonObjectChange = new JSONObject();
    JSONArray jsonArrayChange = new JSONArray();
    String[] sChange;
    String fileName;
    ArrayList<String> myArrList;
    ListView lv;

    String TAG = "Nsd";

    Handler mUpdateHandler;

    String message;

    private Button mRegisterBtn;
    private Button mDiscoverBtn;
    private Button mSayHelloBtn;

    int messageCount = 0;
    RelativeLayout changeRL, gameRL, networkRL;

    // Callbacks for receiving payloads
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    message = new String(payload.asBytes(), UTF_8);
                    Log.i(TAG, message);
                    if (extrasWho=="client") {
                        messageCount++;
                        if (messageCount == 1) {
                            extrasString = message;
                        } else if (messageCount == 2) {
                            seed = Long.parseLong(message);
                            changeRL.setVisibility(View.GONE);
                            networkRL.setVisibility(View.GONE);
                            initializeGame();
                            gameRL.setVisibility(View.VISIBLE);
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
                        Log.i(TAG, "onConnectionResult: connection successful");

                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();

                        opponentEndpointId = endpointId;

                        if (extrasWho == "host"){
                            changeRL.setVisibility(View.VISIBLE);
                            networkRL.setVisibility(View.GONE);
                            gameRL.setVisibility(View.GONE);
                        }

                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_game);

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }

        connectionsClient = Nearby.getConnectionsClient(this);

        changeRL = (RelativeLayout) findViewById(R.id.changelayout);
        gameRL = (RelativeLayout) findViewById(R.id.gamelayout);
        networkRL = (RelativeLayout) findViewById(R.id.networklayout);
        mRegisterBtn = (Button) findViewById(R.id.register);
        mDiscoverBtn = (Button) findViewById(R.id.discover);

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
                onClickChangedGame();
                changeRL.setVisibility(View.GONE);
                networkRL.setVisibility(View.GONE);
                gameRL.setVisibility(View.VISIBLE);
                sendMessage(fileName);
                initializeGame();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
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
        if (extrasWho == "host") {
            randseed = new Random();
            extrasString = fileName;
            randseed = new Random();
            seed = randseed.nextLong();
            sendMessage(Long.toString(seed));
        }
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
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countTime++;
            }
        }, 1);
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
                i.putExtra("time", Integer.toString(countTime));
                startActivity(i);
                i.putExtra("onlineMode", extrasWho);
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

    /** Finds an opponent to play the game with using Nearby Connections. */
    public void findOpponent(View view) {
        startDiscovery();
        startAdvertising();
        extrasWho = "client";
    }

    public void hostGame(View view) {
        startAdvertising();
        extrasWho = "host";
    }

    /** Disconnects from the opponent and reset the UI. */
    public void disconnect(View view) {
        connectionsClient.disconnectFromEndpoint(opponentEndpointId);
    }

    /** Starts looking for other players using Nearby Connections. */
    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startDiscovery(
                getPackageName(), endpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(STRATEGY).build());
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startAdvertising(
                codeName, getPackageName(), connectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build());
    }
    private void sendMessage(String me) {
        connectionsClient.sendPayload(
                opponentEndpointId, Payload.fromBytes(me.getBytes(UTF_8)));
    }

    @Override
    protected void onStop() {
        connectionsClient.stopAllEndpoints();

        super.onStop();
    }

}
