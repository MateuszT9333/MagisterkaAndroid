package com.example.android.bluetoothchat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.common.logger.Log;
import com.example.android.utils.DBHelper;
import com.example.android.utils.LicznikSavedInstance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LicznikActivity extends Activity {
    private static final String msg = "Wiadomosc";
    private TextView textView;
    private int oldId = 0;
    DBHelper dbHelper = new DBHelper(this);
    private Float przejechanyDystansWKilometrach = (float)0; //przejechanyDystansWKilometrach od momentu nacisniecia start
    private boolean tripIsEnabled = false; // jesli start zostal nacisniety
    private int idStartu = 0;
    private long czasJazdyWSekundach = 0;
    private Float predkoscMax = (float)0;
    private boolean tripIsStopped = false;
    private boolean tripIsReset = false;
    private boolean databaseUpdate = true;
    private Float predkoscChwilowa = (float)0;
    private static LicznikSavedInstance licznikSavedInstance;
    private boolean nowaPredkoscChwilowa = false;
    private int newId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licznik);
        pobierzStan();
        oldId = getLatestId();

        Cursor rs = dbHelper.getLatestData(); //odczytaj ostatni rekord z bazy danych
        rs.moveToFirst();
        predkoscChwilowa = aktualizujPredkosc(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_PREDKOSC)));
        aktualizujPola(); // aktualizacja pola
        startAllThreads();
    }
    void startAllThreads(){
        thread.start();
        thread.setPriority(3);
        threadSQL.start();
        threadSQL.setPriority(2);
        threadSQL1.start();
        threadSQL1.setPriority(1);

    }
    private void zapiszStan(){
        licznikSavedInstance = licznikSavedInstance.getInstance();
        licznikSavedInstance.przejechanyDystansWKilometrach = przejechanyDystansWKilometrach;
        licznikSavedInstance.tripIsEnabled = tripIsEnabled;
        licznikSavedInstance.idStartu = idStartu;
        licznikSavedInstance.czasJazdyWSekundach = czasJazdyWSekundach;
        licznikSavedInstance.predkoscMax = predkoscMax;
        licznikSavedInstance.tripIsStopped = tripIsStopped;
    }
    private void pobierzStan(){
        licznikSavedInstance = licznikSavedInstance.getInstance();
        przejechanyDystansWKilometrach = licznikSavedInstance.przejechanyDystansWKilometrach;
        tripIsEnabled = licznikSavedInstance.tripIsEnabled;
        idStartu = licznikSavedInstance.idStartu;
        czasJazdyWSekundach = licznikSavedInstance.czasJazdyWSekundach;
        predkoscMax = licznikSavedInstance.predkoscMax;
        tripIsStopped = licznikSavedInstance.tripIsStopped;
        oldId = getLatestId();
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
            thread.setPriority(2);
        }
        if(!threadSQL.isAlive()){
            threadSQL.start();
            threadSQL.setPriority(3);
        }
        if(!threadSQL1.isAlive()){
            threadSQL1.start();
            threadSQL1.setPriority(1);
        }
        pobierzStan();
        Log.d(msg, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        zapiszStan();
        Log.d(msg, "The onPause() event");
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        zapiszStan();
        Log.d(msg, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        zapiszStan();
        Log.d(msg, "The onDestroy() event");
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(databaseUpdate) {
                                Log.i("Wiadomosc", "aktualizujPola");
                                aktualizujPola();
                                nowaPredkoscChwilowa = false;
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
                                databaseUpdate = isDatabaseUpdate();

                        }
                    });
                    Thread.sleep(400);
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
                                predkoscChwilowa = aktualizujPredkosc(rs.getString(rs.getColumnIndex(DBHelper.BLUETOOTH_COLUMN_PREDKOSC)));
                                nowaPredkoscChwilowa = true;
                                Log.i("Wiadomosc", "nowaPredkoscChwilowa");
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

    private boolean isDatabaseUpdate() {
        newId = getLatestId();
        Log.i("Wiadomosc", "czyBazaUpdatowana?");
        Log.e("NEW ID", String.valueOf(newId));
        Log.e("OLD ID", String.valueOf(oldId));
        if(newId == oldId){
            Log.e("Bazaupdatowana?", "NIE");
            return false;
        }else{
            Log.e("Bazaupdatowana?", "TAK");
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

    private void aktualizujPola()  {
        if(tripIsStopped){
            return; //zamrazanie widoku
        }
        if(tripIsEnabled) {
            aktualizujDystans();
            aktualizujPredkoscSrednia();
            aktualizujPredkoscMax();
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

    private void aktualizujPredkoscMax() {
        if(predkoscChwilowa > predkoscMax){
            predkoscMax = predkoscChwilowa;
        }
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_max);
        textView.setText("MAX: " + String.format("%.2f", predkoscMax) + " km/h");
    }

    private void aktualizujPredkoscSrednia() {
        Float predkoscSrednia = (przejechanyDystansWKilometrach * 1000) / czasJazdyWSekundach; //metry na sekunde
        predkoscSrednia = predkoscSrednia /(float) 3.6;
        textView = (TextView) findViewById(R.id.text_licznik_predkosc_srednia);
        textView.setText("AVG: " + String.format("%.2f", predkoscSrednia) +  " km/h");
    }

    private void aktualizujDystans() {
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

}
