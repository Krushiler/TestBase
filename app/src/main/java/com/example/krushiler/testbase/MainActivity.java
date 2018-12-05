package com.example.krushiler.testbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    public void onClickStart(View v) {
        Intent i = new Intent(this, ChangeTestActivity.class);
        startActivity(i);
    }
    public void onClickSlovari(View v){
        Intent i = new Intent(this, ChangeSlovariActivity.class);
        startActivity(i);
    }
    public void onClickNetworkGame(View v){
        Intent i = new Intent(this, ChangeTestNetActivity.class);
        startActivity(i);
    }
}
