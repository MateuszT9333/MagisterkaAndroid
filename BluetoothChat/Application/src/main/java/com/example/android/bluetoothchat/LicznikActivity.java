package com.example.android.bluetoothchat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.utils.DBHelper;

public class LicznikActivity extends Activity {

    private TextView textView;
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licznik);
        aktualizujPola(dbHelper); // aktualizacja pola
        thread.start();
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
    public void startButtonLicznik(View v) {

    }

    public void stopButtonLicznik(View v) {

    }

    public void resetButtonLicznik(View v) {

    }

    public void powrotButtonLicznik(View v) {
        Intent intent = new Intent(this, BiezacyOdczytActivity.class);
        startActivity(intent);
    }

    public void dalejButtonLicznik(View v) {

    }

    private void aktualizujPola(DBHelper dbHelper) {
        Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();
        pobierzDate(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_DATA)));
        aktualizujPredkosc(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_PREDKOSC)));
        aktualizujDystans();
        aktualizujPredkoscSrednia();
        aktualizujPredkoscMax();
        aktualizujCzasJazdy();

    }

    private void pobierzDate(String string) {
        String dataZGps = string;
    }

    private void aktualizujCzasJazdy() {
        textView = (TextView) findViewById(R.id.text_licznik_czas);
        textView.setText("TIME: " + "5:10:20" );
    }

    private void aktualizujPredkoscMax() {
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_max);
        textView.setText("MAX: " + "25 km/h");
    }

    private void aktualizujPredkoscSrednia() {
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_srednia);
        textView.setText("AVG: " + "21 km/h");
    }

    private void aktualizujDystans() {
        textView = (TextView) findViewById(R.id.text_licznik_dystans);
        textView.setText("DIS: " + "10,52 km");
    }

    private void aktualizujPredkosc(String text){
        textView = (TextView) findViewById(R.id.text_licznik_predkosc);
        textView.setText("SPD: " + text + " km/h");
    }

    private boolean isGPS(){
        Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();
        String dlugosc = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_DLUGOSCGEOGRAFICZNA));
        String szerokosc = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_SZEROKOSCGEOGRAFICZNA));
        boolean isValidDlugosc = dlugosc != "0";
        boolean isValidSzerokosc = szerokosc != "0";
        if(isValidDlugosc && isValidSzerokosc){
            return true;
        }else{
            return false;
        }
    }
}
