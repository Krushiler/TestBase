package com.example.krushiler.testbase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class WinActivity extends AppCompatActivity {

    TextView tv, tvtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        Intent i = getIntent();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault());
        String strDate = i.getStringExtra("time");

//        tvtime = (TextView) findViewById(R.id.tvtime);
        tv =(TextView) findViewById(R.id.textView);

//        tvtime.setText(strDate);
        tv.setText("Ошибки: " + Integer.toString(i.getIntExtra("mistakes", 0)));
    }
}
