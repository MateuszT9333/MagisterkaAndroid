package com.example.android.bluetoothchat;

import com.example.android.common.logger.Log;
import com.example.android.utils.NMEA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mateusz on 14.10.17.
 */

public class SaveToDatabase {
    private static SaveToDatabase instance = null;
    private float  cisnienie;
    private float  temperatura;
    private int  ax;
    private int  ay;
    private int  az;
    private int  gx;
    private int  gy;
    private int  gz;
    private float  napiecie;


    protected SaveToDatabase() {

    }

    public static SaveToDatabase getInstance() {
        if (instance == null) {
            instance = new SaveToDatabase();
        }
        return instance;
    }


    public int addString(String string) {
        parseString(string);
        return string.length();
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
            parseGPRMC(GPRMC);
           // Log.i("GPRMC",GPRMC);

            //wydzielenie cisnienia
            indexOfBegin = joinedString.indexOf("C");
            indexOfEnd = joinedString.indexOf("T",indexOfBegin);
            cisnienie = joinedString.substring(indexOfBegin + 1, indexOfEnd);
            float cisnienieFloat = Float.parseFloat(cisnienie);
            this.cisnienie = cisnienieFloat / 100;
           // Log.i("cisnienie", Float.toString(cisnienieFloat));

            //wydzielenie temperatura
            indexOfBegin = joinedString.indexOf("T");
            indexOfEnd = joinedString.indexOf("ax");
            temperatura = joinedString.substring(indexOfBegin + 1, indexOfEnd);
            float temperaturaFloat = Float.parseFloat(temperatura);
            this.temperatura = temperaturaFloat/10;
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
            float voltageFloat = Float.parseFloat(voltage);
            voltageFloat = voltageFloat * (float) 3.3/1024;
            this.napiecie = voltageFloat;
           // Log.i("napiecie", Float.toString(voltageFloat));
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
        Log.i("Pozycja GPS",gpsPosition.toString());
    }

    public float getTemperatura() {
        return temperatura;
    }

    public float getAx() {
        return ax;
    }

    public float getAy() {
        return ay;
    }

    public float getAz() {
        return az;
    }

    public float getGx() {
        return gx;
    }

    public float getGy() {
        return gy;
    }

    public float getGz() {
        return gz;
    }

    public float getNapiecie() {
        return napiecie;
    }

    public float getCisnienie() {
        return cisnienie;
    }
}


