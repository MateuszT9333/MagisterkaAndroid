package com.example.android.utils;
/**
 * https://gist.github.com/javisantana/1326141
 */

import com.example.android.common.logger.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NMEA {

    interface SentenceParser {
        public boolean parse(String [] tokens, GPSPosition position);
    }

    // utils
    static float Latitude2Decimal(String lat, String NS) {
        float med = Float.parseFloat(lat.substring(2))/60.0f;
        med +=  Float.parseFloat(lat.substring(0, 2));
        if(NS.startsWith("S")) {
            med = -med;
        }
        return med;
    }

    static float Longitude2Decimal(String lon, String WE) {
        float med = Float.parseFloat(lon.substring(3))/60.0f;
        med +=  Float.parseFloat(lon.substring(0, 3));
        if(WE.startsWith("W")) {
            med = -med;
        }
        return med;
    }

    class GPRMC implements SentenceParser {
        public boolean parse(String[] tokens, GPSPosition position) {
            if (!tokens[1].equals("")) {
                position.time = Float.parseFloat(tokens[1]);
            }
            if (!((tokens[3].equals("")) || (tokens[4].equals("")))) {
                position.lat = Latitude2Decimal(tokens[3], tokens[4]);
            }
            if (!((tokens[5].equals("")) || (tokens[6].equals("")))) {
                position.lon = Longitude2Decimal(tokens[5], tokens[6]);
            }
            if (!tokens[7].equals("")) {
                position.velocity = Float.parseFloat(tokens[7]);
            }
            if (!tokens[8].equals("")) {
                position.dir = Float.parseFloat(tokens[8]);
            }
            if (!tokens[9].equals("")) {
                position.day = Float.parseFloat(tokens[9]);
            }

            int time = Math.round(position.time);
            String godzina = Integer.toString(time/10000);
            int minutySekundy = time%10000;
            String minuta = Integer.toString(minutySekundy/100);
            String sekunda = Integer.toString(minutySekundy%100);
            int date = Math.round(position.day);
            String dzien = Integer.toString(date/10000);
            int miesiaceLata = date%10000;
            String miesiac = Integer.toString(miesiaceLata/100);
            String rok = Integer.toString(miesiaceLata%100);
            String dataDelimiter = "-";
            String timeDelimiter = ":";
            String dataString = rok + dataDelimiter + miesiac + dataDelimiter + dzien + " " +
                    godzina + timeDelimiter + minuta + timeDelimiter + sekunda;

            position.dateFromGps = dataString; //Data w formacie yy-mm-dd hh:mm:ss
            SimpleDateFormat formatter = new SimpleDateFormat("yy-mm-dd hh:mm:ss");
            try{
                formatter.parse(dataString);
            }catch (ParseException e){
                e.printStackTrace();
                Date today = Calendar.getInstance().getTime();
                position.dateFromGps = formatter.format(today);
            }
            if(position.dateFromGps.equals("0-0-0 0:0:0")){
                Date today = Calendar.getInstance().getTime();
                position.dateFromGps = formatter.format(today);
            }
            String dzienMiesiacRok = position.dateFromGps.substring(0,position.dateFromGps.indexOf(" "));
            if(dzienMiesiacRok.equals("0-0-0")){
                Date today = Calendar.getInstance().getTime();
                position.dateFromGps = formatter.format(today);
            }
            return true;
        }
    }

    public class GPSPosition {
        public float time = 0.0f;
        public float lat = 0.0f;
        public float lon = 0.0f;
        public boolean fixed = false;
        public int quality = 0;
        public float dir = 0.0f;
        public float altitude = 0.0f;
        public float velocity = 0.0f;
        public float day = 0.0f;
        public void updatefix() {
            fixed = quality > 0;
        }
        public String dateFromGps;

        public String toString() {
            return String.format("POSITION: lat: %f, lon: %f, time: %f,day %f,dir: %f, vel: %f", lat, lon, time, day, dir, velocity);
        }
    }

    GPSPosition position = new GPSPosition();

    private static final Map<String, SentenceParser> sentenceParsers = new HashMap<String, SentenceParser>();

    public NMEA() {
        sentenceParsers.put("GPRMC", new GPRMC());
    }

    public GPSPosition parse(String line) {

        if(line.startsWith("$")) {
            String nmea = line.substring(1);
            String[] tokens = nmea.split(",");
            String type = tokens[0];
            if(sentenceParsers.containsKey(type)) {
                sentenceParsers.get(type).parse(tokens, position);
            }
            position.updatefix();
        }

        return position;
    }
}