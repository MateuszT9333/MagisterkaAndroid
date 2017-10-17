package com.example.android.bluetoothchat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.common.logger.Log;
import com.example.android.utils.DBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class HistoriaActivity extends Activity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private DBHelper dbHelper;
    private Context context;
    ArrayList<String> daneDoGrida = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia);
        dbHelper = new DBHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.listView);

    }
    public void pobierzHistorieClick(View v) {
//        ArrayList<String> rekordy = dbHelper.getAllData();
//        Log.e("WSZYSTKIEDANE",String.valueOf(rekordy));

//        try {
//            File sd = Environment.getExternalStorageDirectory();
//            File data = Environment.getDataDirectory();
//
//            String currentDBPath = "/data/com.android.bluetooth/databases/btopp.db";
//            String backupDBPath = "Bluetooth.db";
//            File currentDB = new File(data, currentDBPath);
//            File backupDB = new File(sd, backupDBPath);
//
//            if (currentDB.exists()) {
//                FileChannel src = new FileInputStream(currentDB).getChannel();
//                FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                dst.transferFrom(src, 0, src.size());
//                src.close();
//                dst.close();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        ArrayList<String> buffer;
        buffer = dbHelper.getDataAsArray();
        daneDoGrida.addAll(buffer);
        adapter = new ArrayAdapter<>(this, R.layout.row, daneDoGrida);
        listView.setAdapter(adapter);
    }
    public void deleteData(View v){
        dbHelper.deleteData();
    }

}



