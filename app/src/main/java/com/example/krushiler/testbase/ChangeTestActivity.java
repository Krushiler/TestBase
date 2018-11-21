package com.example.krushiler.testbase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChangeTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_test);
    }
    public void onClickSubject(View v){
        Intent i = new Intent(this, GameActivity.class);
        if (v.getId() == R.id.buttonbiology){
            i.putExtra("name", "biology.json");
            Log.d("putExtra", "BIO");
        }else if (v.getId() == R.id.buttonchemistry){
            i.putExtra("name", "chemistry.json");
            Log.d("putExtra", "CHEM");
        }else{
            Log.d("putExtra", "OMG");
        }
        startActivity(i);
    }
}