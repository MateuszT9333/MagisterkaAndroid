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
    String dlugoscGeograficzna;
    BluetoothDataParser bluetoothDataParser;
    private String szerokoscGeograficzna;
    private String ax;
    private String ay;
    private String az;
    private String cisnienie;
    private String data;
    private String gx;
    private String gy;
    private String gz;
    private String kierunek;
    private String napiecie;
    private String predkosc;
    private String temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biezacyodczyt);
        zapytajODane(dbHelper);
        aktualizujPola(); // aktualizacja pola
        thread.start();
        threadSQL.start();

    }
    public void onClickDalej(View v){
        thread.interrupt();
        threadSQL.interrupt();
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
        threadSQL.interrupt();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            aktualizujPola();
                        }
                    });
                    Thread.sleep(500);
//                    Log.i("Biezacy odczyt watek", "pola zaktualizowane");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().isInterrupted();
                }
            }
        }
    };
    Thread threadSQL = new Thread() {
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            zapytajODane(dbHelper);
                        }
                    });
                    Thread.sleep(500);
//                    Log.i("Biezacy odczyt watek", "zapytanie o dane");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().isInterrupted();
                }
            }
        }
    };

    private void zapytajODane(DBHelper dbHelper) {
        Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();

        dlugoscGeograficzna = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_SZEROKOSCGEOGRAFICZNA));
        szerokoscGeograficzna = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_DLUGOSCGEOGRAFICZNA));
        ax = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_AX));
        ay = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_AY));
        az = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_AZ));
        cisnienie = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_CISNIENIE));
        data = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_DATA));
        gx = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_GX));
        gy = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_GY));
        gz = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_GZ));
        kierunek = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_KIERUNEK));
        napiecie = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_NAPIECIE));
        predkosc = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_PREDKOSC));
        temperature = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_TEMPERATURA));

    }

    private void aktualizujPola() {

        aktualizujSzerokoscGeograficzna();
        aktualizujDlugoscGeograficzna();
        aktualizujAx();
        aktualizujAy();
        aktualizujAz();
        aktualizujCisnienie();
        aktualizujDate();
        aktualizujGx();
        aktualizujGy();
        aktualizujGz();
        aktualizujKierunek();
        aktualizujNapiecie();
        aktualizujPredkosc();
        aktualizujTemperature();
        aktualizujTextViewGPS();
    }

    private void aktualizujTextViewGPS() {
        TextView textViewGPS = (TextView) findViewById(R.id.text_biezacy_isGPS);
        if(isGPS()){
            textViewGPS.setText("GPS OK");
        }else{
            textViewGPS.setText("NO GPS!");
        }
    }
    private boolean isGPS(){
        Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();
        String isGPS = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_IS_GPS));
        boolean isValidGPS = isGPS.equals("A");
        if(isValidGPS){
            return true;
        }else{
            return false;
        }
    }
    private void aktualizujDlugoscGeograficzna(){
        textView = (TextView) findViewById(R.id.text_biezace_dlugosc);
        textView.setText("Szerokosc: " + dlugoscGeograficzna);
    }
    private void aktualizujSzerokoscGeograficzna(){
        textView = (TextView) findViewById(R.id.text_biezace_szerokosc);
        textView.setText("Długość: " + szerokoscGeograficzna);
    }
    private void aktualizujDate(){
        textView = (TextView) findViewById(R.id.text_biezace_data);
        textView.setText("Data: " + data);
    }
    private void aktualizujKierunek(){
        textView = (TextView) findViewById(R.id.text_biezace_kierunek);
        textView.setText("Kier: " + kierunek + " \u00b0");
    }
    private void aktualizujPredkosc(){
        float predkoscChwilowa = Float.parseFloat(predkosc);
        textView = (TextView) findViewById(R.id.text_biezace_predkosc);
        textView.setText("Prędk: " +String.format("%.3f", predkoscChwilowa) + " km/h");
    }
    private void aktualizujCisnienie(){
        textView = (TextView) findViewById(R.id.text_biezace_cisnienie);
        textView.setText("Ciśn: " + cisnienie + " hPA");
    }
    private void aktualizujTemperature(){
        textView = (TextView) findViewById(R.id.text_biezace_temperatura);
        if(temperature.contains("Inf")){
            textView.setText("Temp:\n " + "0.0" +  " \u00b0" + " C");
        }else {
            textView.setText("Temp:\n " + temperature + " \u00b0" + " C");
        }
    }
    private void aktualizujAx(){
        float skalibrowana = Float.parseFloat(ax);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*90 - offsetAx;
        textView = (TextView) findViewById(R.id.text_biezace_ax);
        textView.setText("AX: " + String.format("%.2f", skalibrowana )+  " \u00b0");
    }
    private void aktualizujAy(){
        float skalibrowana = Float.parseFloat(ay);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*90 - offsetAy;
        textView = (TextView) findViewById(R.id.text_biezace_ay);
        textView.setText("AY: " + String.format("%.2f", skalibrowana ) +  " \u00b0");
    }
    private void aktualizujAz(){
        float skalibrowana = Float.parseFloat(az);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = (skalibrowana/16384*90) - offsetAz;
        textView = (TextView) findViewById(R.id.text_biezace_az);
        textView.setText("AZ: " + String.format("%.2f", skalibrowana ) +  " \u00b0");
    }
    private void aktualizujGx(){
        float skalibrowana = Float.parseFloat(gx);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*2 + 1;
        textView = (TextView) findViewById(R.id.text_biezace_gx);
        textView.setText("GX: " + String.format("%.2f", skalibrowana ) + " G");
    }
    private void aktualizujGy(){
        float skalibrowana = Float.parseFloat(gy);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*2 + 1;
        textView = (TextView) findViewById(R.id.text_biezace_gy);
        textView.setText("GY: " + String.format("%.2f", skalibrowana ) + " G");
    }
    private void aktualizujGz(){
        float skalibrowana = Float.parseFloat(gz);
        if(skalibrowana > 16384 ) skalibrowana=16384;
        skalibrowana = skalibrowana/16384*2 + 1;
        textView = (TextView) findViewById(R.id.text_biezace_gz);
        textView.setText("GZ:" + String.format("%.2f", skalibrowana ) + " G");
    }
    private void aktualizujNapiecie(){
        float skalibrowana = (float)Double.parseDouble(napiecie);
        skalibrowana = skalibrowana * (float) 2;
        textView = (TextView) findViewById(R.id.text_biezace_napiecie);
        textView.setText("Nap:\n" + String.format("%.2f", skalibrowana ) + " V");
    }



}
