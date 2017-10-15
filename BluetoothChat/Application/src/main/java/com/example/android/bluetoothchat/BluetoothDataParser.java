package com.example.android.bluetoothchat;

import com.example.android.common.logger.Log;
import com.example.android.utils.DBHelper;
import com.example.android.utils.NMEA;

import java.util.Date;

/**
 * Created by mateusz on 14.10.17.
 */

public class BluetoothDataParser {
    private static BluetoothDataParser instance = null;
    public Float szerokoscGeograficzna;
    public Float  dlugoscGeograficzna;
    public Date dateFromGPS;
    public Float kierunek;
    public Float predkosc;
    public Float  cisnienie;
    public Float  temperatura;
    public Integer  ax;
    public Integer  ay;
    public Integer  az;
    public Integer  gx;
    public Integer  gy;
    public Integer  gz;
    public Float  napiecie;
    public DBHelper dbHelper = null;


    protected BluetoothDataParser() {

    }

    public static BluetoothDataParser getInstance() {

        if (instance == null) {
            instance = new BluetoothDataParser();
        }
        return instance;
    }


    public int addString(String string, DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        parseString(string);
        if(string.substring(0,30).contains("A")) return 1;
        else return 0;
    }

    private void parseString(String joinedString) {
        String endWord = new String("END");
        String GPRMC = "";
        String cisnienie = "";
        String temperatura = "";
        String ax="";
        String ay="";
        String az="";
        String gx="";
        String gy="";
        String gz="";
        String voltage="";

        int indexOfBegin = 0;
        int indexOfEnd = 0;
        int indexOfEndWord = joinedString.indexOf(endWord);
        try {
            joinedString = joinedString.substring(indexOfEndWord);
            indexOfEndWord = joinedString.indexOf(endWord, 4);
            joinedString = joinedString.substring(3, indexOfEndWord);
            // Log.i("message",joinedString);

            //wydzielenie GPRMC
            indexOfBegin = joinedString.indexOf(",");
            indexOfEnd = joinedString.indexOf("C",indexOfBegin);
            GPRMC = joinedString.substring(indexOfBegin, indexOfEnd);
           // Log.i("GPRMC",GPRMC);

            //wydzielenie cisnienia
            indexOfBegin = joinedString.indexOf("C");
            indexOfEnd = joinedString.indexOf("T",indexOfBegin);
            cisnienie = joinedString.substring(indexOfBegin + 1, indexOfEnd);
            if(!cisnienie.contains("C")) {
                float cisnienieFloat = Float.parseFloat(cisnienie);
                this.cisnienie = cisnienieFloat / 100;
            }
           // Log.i("cisnienie", Float.toString(cisnienieFloat));

            //wydzielenie temperatura
            indexOfBegin = joinedString.indexOf("T");
            indexOfEnd = joinedString.indexOf("ax");
            temperatura = joinedString.substring(indexOfBegin + 1, indexOfEnd);
            if(!temperatura.contains("T")) {
                float temperaturaFloat = Float.parseFloat(temperatura);
                this.temperatura = temperaturaFloat / 10;
            }
           // Log.i("temperatura", Float.toString(this.temperatura));

            //wydzielenie ax
            indexOfBegin = joinedString.indexOf("ax");
            indexOfEnd = joinedString.indexOf("ay",indexOfBegin);
            ax = joinedString.substring(indexOfBegin + 2, indexOfEnd);
            ax=ax.substring(0, ax.indexOf("\n"));
            int axInt = Integer.parseInt(ax);
            this.ax=axInt;
          //  Log.i("ax", Integer.toString(axInt));

            //wydzielenie ay
            indexOfBegin = joinedString.indexOf("ay");
            indexOfEnd = joinedString.indexOf("az",indexOfBegin);
            ay = joinedString.substring(indexOfBegin + 2, indexOfEnd);
            ay=ay.substring(0, ay.indexOf("\n"));
            int ayInt = Integer.parseInt(ay);
            this.ay=ayInt;
          //  Log.i("ay", Integer.toString(ayInt));

            //wydzielenie az
            indexOfBegin = joinedString.indexOf("az");
            indexOfEnd = joinedString.indexOf("gx",indexOfBegin);
            az = joinedString.substring(indexOfBegin + 2, indexOfEnd);
            az=az.substring(0, az.indexOf("\n"));
            int azInt = Integer.parseInt(az);
            this.az=azInt;
          //  Log.i("az", Integer.toString(azInt));

            //wydzielenie gx
            indexOfBegin = joinedString.indexOf("gx");
            indexOfEnd = joinedString.indexOf("gy",indexOfBegin);
            gx = joinedString.substring(indexOfBegin + 2, indexOfEnd);
            gx=gx.substring(0, gx.indexOf("\n"));
            int gxInt = Integer.parseInt(gx);
            this.gx = gxInt;
          //  Log.i("gx", Integer.toString(gxInt));

            //wydzielenie gy
            indexOfBegin = joinedString.indexOf("gy");
            indexOfEnd = joinedString.indexOf("gz",indexOfBegin);
            gy = joinedString.substring(indexOfBegin + 2, indexOfEnd);
            gy=gy.substring(0, gy.indexOf("\n"));
            int gyInt = Integer.parseInt(gy);
            this.gy = gyInt;
          //  Log.i("gy", Integer.toString(gyInt));

            //wydzielenie gz
            indexOfBegin = joinedString.indexOf("gz");
            indexOfEnd = joinedString.indexOf("V",indexOfBegin);
            gz = joinedString.substring(indexOfBegin + 2, indexOfEnd);
            gz=gz.substring(0, gz.indexOf("\n"));
            int gzInt = Integer.parseInt(gz);
            this.gz = gzInt;
            //Log.i("gz", Integer.toString(gzInt));

            //wydzielenie napiecia
            indexOfBegin = joinedString.indexOf("V",indexOfEnd);
            voltage = joinedString.substring(indexOfBegin + 1);
            if(!voltage.contains("V")) {
                float voltageFloat = Float.parseFloat(voltage);
                voltageFloat = voltageFloat * (float) 3.3 / 1024;
                this.napiecie = voltageFloat;
            }
           // Log.i("napiecie", Float.toString(voltageFloat));
            parseGPRMC(GPRMC);
        }
        catch (Exception e){
            e.printStackTrace(System.out);
            return;
        }
    }

    private void parseGPRMC(String gprmc) {
        gprmc = "$GPRMC" + gprmc;
        NMEA nmea = new NMEA();
        NMEA.GPSPosition gpsPosition;
        gpsPosition =  nmea.parse(gprmc);
        this.szerokoscGeograficzna = gpsPosition.lat;
        this.dlugoscGeograficzna = gpsPosition.lon;
        this.dateFromGPS = gpsPosition.dateFromGps;
        this.predkosc = gpsPosition.velocity * (float)1.852;
        this.kierunek = gpsPosition.dir;

        Log.i("Szerokosc GPS", String.valueOf(this.szerokoscGeograficzna));
        Log.i("Dlugosc GPS", String.valueOf(this.dlugoscGeograficzna));
        Log.i("Data", String.valueOf(this.dateFromGPS));
        Log.i("Predkosc", String.valueOf(this.predkosc));
        Log.i("Kierunek", String.valueOf(this.kierunek));
        Log.i("Cisnienie", String.valueOf(this.cisnienie));
        Log.i("Temperatura", String.valueOf(this.temperatura));
        Log.i("ax", String.valueOf(this.ax));
        Log.i("ay", String.valueOf(this.ay));
        Log.i("az", String.valueOf(this.az));
        Log.i("gx", String.valueOf(this.gx));
        Log.i("gy", String.valueOf(this.gy));
        Log.i("gz", String.valueOf(this.gz));
        Log.i("Napiecie", String.valueOf(this.napiecie));
        dbHelper.insertData(this);

    }

}


