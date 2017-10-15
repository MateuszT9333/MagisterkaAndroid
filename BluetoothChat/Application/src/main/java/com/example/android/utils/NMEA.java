package com.example.android.utils;
/**
 * https://gist.github.com/javisantana/1326141
 */

import com.example.android.common.logger.Log;

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
                tokens[1] = tokens[1].substring(0,tokens[1].indexOf("."));
                position.time = Integer.parseInt(tokens[1]);
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
                position.day = Integer.parseInt(tokens[9]);
            }
            return true;
        }
    }

    public class GPSPosition {
        public int time = 0;
        public float lat = 0.0f;
        public float lon = 0.0f;
        public boolean fixed = false;
        public int quality = 0;
        public float dir = 0.0f;
        public float altitude = 0.0f;
        public float velocity = 0.0f;
        public int day = 0;

        public void updatefix() {
            fixed = quality > 0;
        }

        public String toString() {
            return String.format("POSITION: lat: %f, lon: %f, time: %d,day %d, Q: %d, dir: %f, alt: %f, vel: %f", lat, lon, time, day, quality, dir, altitude, velocity);
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