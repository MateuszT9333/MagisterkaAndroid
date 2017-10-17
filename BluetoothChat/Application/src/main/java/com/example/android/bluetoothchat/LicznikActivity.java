package com.example.android.bluetoothchat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.common.logger.Log;
import com.example.android.utils.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LicznikActivity extends Activity {
    private static final String DYSTANS = "przejechanyDystansWKilometrach";
    private static final String TRIP_IS_ENABLED = "tripIsEnabled";
    private static final String ID_STARTU = "idStartu";
    private static final String CZAS_JAZDY_W_MILISEKUNDACH = "czasJazdyWSekundach";
    private static final String PREDKOSC_MAX = "predkoscMax";
    private static final String TRIP_IS_STOPPED = "tripIsStopped";
    private static final String msg = "Wiadomosc";
    private TextView textView;
    private static int oldId;
    DBHelper dbHelper = new DBHelper(this);
    private Float przejechanyDystansWKilometrach; //przejechanyDystansWKilometrach od momentu nacisniecia start
    private boolean tripIsEnabled; // jesli start zostal nacisniety
    private int idStartu;
    private long czasJazdyWSekundach;
    private Float predkoscMax;
    private boolean tripIsStopped = false;
    private boolean tripIsReset;
    private boolean databaseUpdate = true;
    private Float predkoscChwilowa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            przejechanyDystansWKilometrach = savedInstanceState.getFloat(LicznikActivity.DYSTANS);
            tripIsEnabled = savedInstanceState.getBoolean(LicznikActivity.TRIP_IS_ENABLED);
            idStartu = savedInstanceState.getInt(LicznikActivity.ID_STARTU);
            czasJazdyWSekundach = savedInstanceState.getLong(LicznikActivity.CZAS_JAZDY_W_MILISEKUNDACH);
            predkoscMax = savedInstanceState.getFloat(LicznikActivity.PREDKOSC_MAX);
            tripIsStopped = savedInstanceState.getBoolean(LicznikActivity.TRIP_IS_STOPPED);
        }else {
            przejechanyDystansWKilometrach= (float) 0;
            czasJazdyWSekundach = 0;
            predkoscMax = (float) 0;
        }

        oldId = getLatestId();
        setContentView(R.layout.activity_licznik);
        aktualizujPola(dbHelper); // aktualizacja pola
        startAllThreads();
    }
    void startAllThreads(){
        thread.start();
        threadSQL.start();
        threadSQL1.start();

    }
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(msg, "The onStart() event");
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
        if(!thread.isAlive()){
            thread.start();
        }
        if(!threadSQL.isAlive()){
            threadSQL.start();
        }
        if(!threadSQL1.isAlive()){
            threadSQL1.start();
        }
        Log.d(msg, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg, "The onPause() event");
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(msg, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(msg, "The onDestroy() event");
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState){
//        if(savedInstanceState != null){
//            przejechanyDystansWKilometrach = savedInstanceState.getFloat(LicznikActivity.DYSTANS);
//            tripIsEnabled = savedInstanceState.getBoolean(LicznikActivity.TRIP_IS_ENABLED);
//            idStartu = savedInstanceState.getInt(LicznikActivity.ID_STARTU);
//            czasJazdyWSekundach = savedInstanceState.getLong(LicznikActivity.CZAS_JAZDY_W_MILISEKUNDACH);
//            predkoscMax = savedInstanceState.getFloat(LicznikActivity.PREDKOSC_MAX);
//            tripIsStopped = savedInstanceState.getBoolean(LicznikActivity.TRIP_IS_STOPPED);
//        }
//        super.onRestoreInstanceState(savedInstanceState);
//
//    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(databaseUpdate) {
                                aktualizujPola(dbHelper);
                            }
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Thread threadSQL1 = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(databaseUpdate) {
                                databaseUpdate = isDatabaseUpdate();                            }
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Thread threadSQL = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(databaseUpdate) {
                                Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
                                rs.moveToFirst();
                                predkoscChwilowa = aktualizujPredkosc(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_PREDKOSC)));                            }
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private boolean isDatabaseUpdate() {
        int newId = getLatestId();
        if(newId == oldId){
            return false;
        }else{
            oldId = newId;
            return true;
        }
    }

    public void startButtonLicznik(View v) {
        tripIsEnabled = true;
        if(!tripIsStopped) {
            idStartu = getLatestId();
        }
        tripIsStopped=false;

    }

    private int getLatestId() {
        Cursor rs = dbHelper.getLatestId(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();
        return Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_ID))); //pobierz ostatnie id
    }

    public void stopButtonLicznik(View v) {
        if(!tripIsEnabled){
            return;
        }
        tripIsStopped = true;
    }

    public void resetButtonLicznik(View v) {
        tripIsReset = true;
        tripIsEnabled = false;
        this.przejechanyDystansWKilometrach =(float) 0.0;
        this.predkoscMax=(float) 0.0;
        this.czasJazdyWSekundach = 0;
        this.tripIsEnabled = false;
    }

    public void powrotButtonLicznik(View v) {
        Intent intent = new Intent(this, BiezacyOdczytActivity.class);
        startActivity(intent);
    }

    public void dalejButtonLicznik(View v) {
    }

    private void aktualizujPola(DBHelper dbHelper)  {
        if(tripIsStopped){
            return; //zamrazanie widoku
        }
        if(tripIsEnabled) {
            aktualizujDystans(predkoscChwilowa);
            aktualizujPredkoscSrednia(przejechanyDystansWKilometrach, czasJazdyWSekundach);
            aktualizujPredkoscMax(predkoscChwilowa);
            aktualizujCzasJazdy();
        }else{
            aktualizujPustyDystans();
            aktualizujPustaPredkoscSrednia();
            aktualizujPustaPredkoscMax();
            aktualizujPustaCzasJazdy();
        }
        if(tripIsReset){
            aktualizujPustyDystans();
            aktualizujPustaPredkoscSrednia();
            aktualizujPustaPredkoscMax();
            aktualizujPustaCzasJazdy();
            tripIsReset=false;
        }
        aktualizujTextViewGPS();

    }

    private void aktualizujPustaCzasJazdy() {
        textView = (TextView) findViewById(R.id.text_licznik_czas);
        textView.setText("TIME: " + "0:0:0" );
    }

    private void aktualizujPustaPredkoscMax() {
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_max);
        textView.setText("MAX: " + "0 km/h");
    }

    private void aktualizujPustaPredkoscSrednia() {
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_srednia);
        textView.setText("AVG: " + "0 km/h");
    }

    private void aktualizujPustyDystans() {
        textView = (TextView) findViewById(R.id.text_licznik_dystans);
        textView.setText("DIS: " + "0 km");
    }

    private void aktualizujTextViewGPS() {
        TextView textViewGPS = (TextView) findViewById(R.id.text_licznik_isGPS);
        if(isGPS()){
            textViewGPS.setText("GPS OK");
        }else{
            textViewGPS.setText("NO GPS!");
        }
    }


    private void aktualizujCzasJazdy() {
        czasJazdyWSekundach += 1;

        Calendar czasJazdy = Calendar.getInstance();
        czasJazdy.setTimeInMillis(czasJazdyWSekundach * 1000);

        int hours = czasJazdy.get(Calendar.HOUR);
        if(hours == 1) hours=0; //TODO usun
        int minutes = czasJazdy.get(Calendar.MINUTE);
        int seconds = czasJazdy.get(Calendar.SECOND);

        textView = (TextView) findViewById(R.id.text_licznik_czas);
        textView.setText("TIME: " + hours + ":" + minutes + ":" + seconds);

    }

    private void aktualizujPredkoscMax(Float predkoscChwilowa) {
        if(predkoscChwilowa > predkoscMax){
            predkoscMax = predkoscChwilowa;
        }
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_max);
        textView.setText("MAX: " + String.format("%.2f", predkoscMax) + " km/h");
    }

    private void aktualizujPredkoscSrednia(Float przejechanyDystansWKilometrach, long czasJazdyWSekundach) {
        Float predkoscSrednia = (przejechanyDystansWKilometrach * 1000) / czasJazdyWSekundach; //metry na sekunde
        predkoscSrednia = predkoscSrednia /(float) 3.6;
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_srednia);
        textView.setText("AVG: " + String.format("%.2f", predkoscSrednia) +  " km/h");
    }

    private void aktualizujDystans(Float predkoscChwilowa) {
        Float dystansWMetrach = predkoscChwilowa/(float)3.6; // przejechanyDystansWKilometrach przejechany w ciagu sekundy
        Float dystansWKilometrach = dystansWMetrach /(float) 1000; // przejechanyDystansWKilometrach w ciagu sekundy w kilometrach
        przejechanyDystansWKilometrach += dystansWKilometrach;
        textView = (TextView) findViewById(R.id.text_licznik_dystans);
        textView.setText("DIS: " + String.format("%.3f", przejechanyDystansWKilometrach) + " km");
    }

    private Float aktualizujPredkosc(String text){
        textView = (TextView) findViewById(R.id.text_licznik_predkosc);
        textView.setText("SPD: " + text + " km/h");
        return Float.parseFloat(text);
    }

    private boolean isGPS(){
        Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();
        String dlugosc = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_DLUGOSCGEOGRAFICZNA));
        String szerokosc = rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_SZEROKOSCGEOGRAFICZNA));
        boolean isValidDlugosc = !dlugosc.equals("0");
        boolean isValidSzerokosc = !szerokosc.equals("0");
        if(isValidDlugosc && isValidSzerokosc){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putFloat(LicznikActivity.DYSTANS, przejechanyDystansWKilometrach);
        savedInstanceState.putBoolean(LicznikActivity.TRIP_IS_ENABLED, tripIsEnabled);
        savedInstanceState.putInt(LicznikActivity.ID_STARTU, idStartu);
        savedInstanceState.putLong(LicznikActivity.CZAS_JAZDY_W_MILISEKUNDACH, czasJazdyWSekundach);
        savedInstanceState.putFloat(LicznikActivity.PREDKOSC_MAX, predkoscMax);
        savedInstanceState.putBoolean(LicznikActivity.TRIP_IS_STOPPED,tripIsStopped);
        super.onSaveInstanceState(savedInstanceState);

    }
}
