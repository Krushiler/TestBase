package com.example.krushiler.testbase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.krushiler.testbase.connection.NSDDiscover;
import com.example.krushiler.testbase.connection.NSDListen;

public class NetworkGameActivity extends AppCompatActivity {

    public NSDDiscover mNSDDiscover;
    public NSDListen mNSDListener;

    private Button mRegisterBtn;
    private Button mDiscoverBtn;
    private Button mSayHelloBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_game);
        mNSDListener = new NSDListen(this);
        mNSDDiscover = new NSDDiscover(this, mDiscoveryListener);
        final Intent intentToChangeTest = new Intent(this, ChangeTestNetActivity.class);
        mRegisterBtn = (Button) findViewById(R.id.register);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDListener.registerDevice();
                findViewById(R.id.discover).setEnabled(false);
                startActivity(intentToChangeTest);
            }
        });

        mDiscoverBtn = (Button) findViewById(R.id.discover);
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDDiscover.discoverServices();
                mRegisterBtn.setEnabled(false);
            }
        });

        mSayHelloBtn = (Button) findViewById(R.id.sayHello);
        mSayHelloBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDDiscover.sayHello();
            }
        });
    }

    private NSDDiscover.DiscoveryListener mDiscoveryListener = new NSDDiscover.DiscoveryListener() {
        @Override
        public void serviceDiscovered(String host, int port) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNSDListener.shutdown();
        mNSDDiscover.shutdown();
    }
}
