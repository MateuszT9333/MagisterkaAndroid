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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia);
        dbHelper = new DBHelper(getApplicationContext());
//        listView = (ListView) findViewById(R.id.listView);
//        ArrayList<String> cars = new ArrayList<>();
//        String carsy[] = {"ax: 90.1","ay: 80.1", "az: -90.1", "gx: 1.0",
//                "gy: 1.1", "gz: 1.2", "dl: 50.4569", "sze: 18.5545",
//                "Pred. : 5.04", "Kier: 81", "Data: 15-06-17 5:4:2"};
//        String carsy2[] = {"ax: 80.1","ay: 80.1", "az: -90.1", "gx: 1.0",
//                "gy: 1.1", "gz: 1.2", "dl: 50.4569", "sze: 18.5545",
//                "Pred. : 6.04", "Kier: 81", "Data: 16-06-17 5:4:2"};
//
//        ArrayList<String> daneDoGrida = new ArrayList<>();
//        daneDoGrida.add(String.valueOf(cars));
//            cars.clear();
//            cars.addAll(Arrays.asList(carsy2));
//        daneDoGrida.add(String.valueOf(cars));
//
//
//        adapter = new ArrayAdapter<>(this, R.layout.row, daneDoGrida);
//
//        listView.setAdapter(adapter);
    }
    public void pobierzHistorieClick(View v) {
        String editext = "17-10-17"; //data z editextu
        //pobierzWszystkieRekordyODacie(editext);
//        ArrayList<String> rekordy = dbHelper.getAllData();
//        Log.e("WSZYSTKIEDANE",String.valueOf(rekordy));

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            String currentDBPath = "/data/com.android.bluetooth/databases/btopp.db";
            String backupDBPath = "Bluetooth.db";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void wyswietlListeStringow(){

    }

}



