package com.example.krushiler.testbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WinActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        tv =(TextView) findViewById(R.id.textView);
        Intent i = getIntent();
        tv.setText(Integer.toString(i.getIntExtra("mistakes", 0)));
    }
}
