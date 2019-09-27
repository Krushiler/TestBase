package com.example.krushiler.testbase;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Handler handler = new Handler();
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menumain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.spravka){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Справка");
            alertDialog.setMessage("Программа не требует интернет соединения\n\nНАЧАТЬ ИГРУ - игра с людьми, находящимися рядом с вами. Один игрок нажимает на кнопку \"хост\", остальные - на кнопку \"поиск\". По умолчанию 10 вопросов. Хост может изменить их количество \n\n" +
                    "ТРЕНИРОВКА - одиночная игра\n\n" +
                    "СЛОВАРИ - термины из тестов\n\n Разработчик - Lazarev Daniil");
            alertDialog.setIcon(R.drawable.information);

            alertDialog.setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alertDialog.show();
        }
        return true;
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
        Intent i = new Intent(this, NetworkGameActivity.class);
        startActivity(i);
    }
}