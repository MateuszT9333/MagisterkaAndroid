package com.example.android.bluetoothchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.utils.DBHelper;

public class HistoriaActivity extends Activity {
    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia);
        dbHelper = new DBHelper(getApplicationContext());

    }
    public void deleteData(View v){
        dbHelper.deleteData();
    }
    public void clickPowrot(View v) {
        Intent intent = new Intent(this, LicznikActivity.class);
        startActivity(intent);
    }

}



