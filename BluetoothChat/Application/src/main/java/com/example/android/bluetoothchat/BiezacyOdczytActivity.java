package com.example.android.bluetoothchat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.renderscript.Float2;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.logger.Log;
import com.example.android.utils.DBHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.offset;

public class BiezacyOdczytActivity extends Activity {
    private TextView textView;
    Handler handler = new Handler();
    DBHelper dbHelper = new DBHelper(this);
    Float offsetAx = new Float(0.0);
    Float offsetAy = new Float(0.0);
    Float offsetAz = new Float(0.0);
    BluetoothDataParser bluetoothDataParser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biezacyodczyt);
        aktualizujPola(dbHelper); // aktualizacja pola
        thread.start();

    }
    public void onClickDalej(View v){
        thread.interrupt();
        Intent intent = new Intent(this, LicznikActivity.class);
        startActivity(intent);
    }
    public void onClickKalibracja(View v){ //kalibrorowania akcelerometru
        textView = (TextView) findViewById(R.id.text_biezace_ax);
        String num =(String) textView.getText();

        Pattern intsOnly = Pattern.compile("[+-]?([0-9]*[,])?[0-9]+");
        Matcher makeMatch = intsOnly.matcher(num);
        makeMatch.find();
        String result = makeMatch.group().replace(',','.');
        offsetAx += Float.parseFloat(result);

        textView = (TextView) findViewById(R.id.text_biezace_ay);
        num =(String) textView.getText();

        intsOnly = Pattern.compile("[+-]?([0-9]*[,])?[0-9]+");
        makeMatch = intsOnly.matcher(num);
        makeMatch.find();
        result = makeMatch.group().replace(',','.');
        offsetAy += Float.parseFloat(result);

        textView = (TextView) findViewById(R.id.text_biezace_az);
        num =(String) textView.getText();
        intsOnly = Pattern.compile("[+-]?([0-9]*[,])?[0-9]+");
        makeMatch = intsOnly.matcher(num);
        makeMatch.find();
        result = makeMatch.group().replace(',','.');
        offsetAz += Float.parseFloat(result);
    }
    public void onClickPowrot(View v){
        thread.interrupt();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
                    Thread.sleep(500);
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
        float skalibrowana = Float.parseFloat(text);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*90 - offsetAx;
        textView = (TextView) findViewById(R.id.text_biezace_ax);
        textView.setText("AX: " + String.format("%.2f", skalibrowana )+  " \u00b0");
    }
    private void aktualizujAy(String text){
        float skalibrowana = Float.parseFloat(text);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*90 - offsetAy;
        textView = (TextView) findViewById(R.id.text_biezace_ay);
        textView.setText("AY: " + String.format("%.2f", skalibrowana ) +  " \u00b0");
    }
    private void aktualizujAz(String text){
        float skalibrowana = Float.parseFloat(text);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = (skalibrowana/16384*90) - offsetAz;
        textView = (TextView) findViewById(R.id.text_biezace_az);
        textView.setText("AZ: " + String.format("%.2f", skalibrowana ) +  " \u00b0");
    }
    private void aktualizujGx(String text){
        float skalibrowana = Float.parseFloat(text);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*2 + 1;
        textView = (TextView) findViewById(R.id.text_biezace_gx);
        textView.setText("GX: " + String.format("%.2f", skalibrowana ) + " G");
    }
    private void aktualizujGy(String text){
        float skalibrowana = Float.parseFloat(text);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*2 + 1;
        textView = (TextView) findViewById(R.id.text_biezace_gy);
        textView.setText("GY: " + String.format("%.2f", skalibrowana ) + " G");
    }
    private void aktualizujGz(String text){
        float skalibrowana = Float.parseFloat(text);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*2 + 1;
        textView = (TextView) findViewById(R.id.text_biezace_gz);
        textView.setText("GZ:" + String.format("%.2f", skalibrowana ) + " G");
    }
    private void aktualizujNapiecie(String text){
        float skalibrowana = Float.parseFloat(text);
        skalibrowana = skalibrowana * 3;
        textView = (TextView) findViewById(R.id.text_biezace_napiecie);
        textView.setText("Napięcie:\n" + String.format("%.2f", skalibrowana ) + " V");
    }



}
