package com.example.android.bluetoothchat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.common.logger.Log;
import com.example.android.utils.DBHelper;

public class BiezacyOdczytActivity extends Activity {
    private TextView textView;
    Handler handler = new Handler();
    DBHelper dbHelper = new DBHelper(this);
    BluetoothDataParser bluetoothDataParser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biezacyodczyt);
        aktualizujPola(dbHelper); // aktualizacja pola
        thread.start();

        final Button button = (Button) findViewById(R.id.button_biezacy_powrot);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             //   Intent intent = new Intent(, MainActivity.class);
             //   startActivity(intent);
            }
        });
        final Button button2 = (Button) findViewById(R.id.button_biezacy_dalej);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), TripActivity.class);
//                startActivity(intent);
            }
        });
        final Button button3 = (Button) findViewById(R.id.button_biezacy_kalibracja);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        });
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            aktualizujPola(dbHelper);
                        }
                    });
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void aktualizujPola(DBHelper dbHelper) {
        Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();

        aktualizujSzerokoscGeograficzna(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_SZEROKOSCGEOGRAFICZNA)));
        aktualizujDlugoscGeograficzna(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_DLUGOSCGEOGRAFICZNA)));
        aktualizujAx(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_AX)));
        aktualizujAy(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_AY)));
        aktualizujAz(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_AZ)));
        aktualizujCisnienie(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_CISNIENIE)));
        aktualizujDate(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_DATA)));
        aktualizujGx(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_GX)));
        aktualizujGy(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_GY)));
        aktualizujGz(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_GZ)));
        aktualizujKierunek(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_KIERUNEK)));
        aktualizujNapiecie(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_NAPIECIE)));
        aktualizujPredkosc(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_PREDKOSC)));
        aktualizujTemperature(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_TEMPERATURA)));
    }
    private void aktualizujDlugoscGeograficzna(String text){
        textView = (TextView) findViewById(R.id.text_biezace_dlugosc);
        textView.setText("Szerokosc: " + text);
    }
    private void aktualizujSzerokoscGeograficzna(String text){
        textView = (TextView) findViewById(R.id.text_biezace_szerokosc);
        textView.setText("Długość: " + text);
    }
    private void aktualizujDate(String text){
        textView = (TextView) findViewById(R.id.text_biezace_data);
        textView.setText("Data: " + text);
    }
    private void aktualizujKierunek(String text){
        textView = (TextView) findViewById(R.id.text_biezace_kierunek);
        textView.setText("Kierunek: " + text + " \u00b0");
    }
    private void aktualizujPredkosc(String text){
        textView = (TextView) findViewById(R.id.text_biezace_predkosc);
        textView.setText("Prędkość: " +text + " km/h");
    }
    private void aktualizujCisnienie(String text){
        textView = (TextView) findViewById(R.id.text_biezace_cisnienie);
        textView.setText("Ciśnienie: " + text + " hPA");
    }
    private void aktualizujTemperature(String text){
        textView = (TextView) findViewById(R.id.text_biezace_temperatura);
        textView.setText("Temperatura: " + text +  " \u00b0" + " C");
    }
    private void aktualizujAx(String text){
        textView = (TextView) findViewById(R.id.text_biezace_ax);
        textView.setText("AX: " + text +  " \u00b0");
    }
    private void aktualizujAy(String text){
        textView = (TextView) findViewById(R.id.text_biezace_ay);
        textView.setText("AY: " + text +  " \u00b0");
    }
    private void aktualizujAz(String text){
        textView = (TextView) findViewById(R.id.text_biezace_az);
        textView.setText("AZ: " + text +  " \u00b0");
    }
    private void aktualizujGx(String text){
        textView = (TextView) findViewById(R.id.text_biezace_gx);
        textView.setText("GX: " + text + " G");
    }
    private void aktualizujGy(String text){
        textView = (TextView) findViewById(R.id.text_biezace_gy);
        textView.setText("GY: " + text + " G");
    }
    private void aktualizujGz(String text){
        textView = (TextView) findViewById(R.id.text_biezace_gz);
        textView.setText("GZ:" + text + " G");
    }
    private void aktualizujNapiecie(String text){
        textView = (TextView) findViewById(R.id.text_biezace_napiecie);
        textView.setText("Napięcie: " + text + " V");
    }



}
