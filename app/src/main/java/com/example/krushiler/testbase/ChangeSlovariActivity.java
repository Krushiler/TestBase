package com.example.krushiler.testbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ChangeSlovariActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_slovari);
    }
    public void onClickChangedSlovar(View v){
        Intent i = new Intent(this, SlovarActivity.class);
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
