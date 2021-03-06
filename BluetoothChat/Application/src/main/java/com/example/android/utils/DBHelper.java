package com.example.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bluetoothchat.BluetoothDataParser;

import java.util.ArrayList;

/**
 * @author Mateusz Trzeciak
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Bluetooth.db";
    public static final String BLUETOOTH_TABLE_NAME = "Data";
    public static final String BLUETOOTH_COLUMN_ID = "id";
    public static final String BLUETOOTH_COLUMN_SZEROKOSCGEOGRAFICZNA = "szerokosc";
    public static final String BLUETOOTH_COLUMN_DLUGOSCGEOGRAFICZNA= "dlugosc";
    public static final String BLUETOOTH_COLUMN_DATA = "data";
    public static final String BLUETOOTH_COLUMN_KIERUNEK = "kierunek";
    public static final String BLUETOOTH_COLUMN_PREDKOSC = "predkosc";
    public static final String BLUETOOTH_COLUMN_CISNIENIE= "cisnienie";
    public static final String BLUETOOTH_COLUMN_TEMPERATURA = "temperatura";
    public static final String BLUETOOTH_COLUMN_AX = "ax";
    public static final String BLUETOOTH_COLUMN_AY = "ay";
    public static final String BLUETOOTH_COLUMN_AZ = "az";
    public static final String BLUETOOTH_COLUMN_GX = "gx";
    public static final String BLUETOOTH_COLUMN_GY = "gy";
    public static final String BLUETOOTH_COLUMN_GZ = "gz";
    public static final String BLUETOOTH_COLUMN_NAPIECIE = "napiecie";
    public static final String BLUETOOTH_COLUMN_IS_GPS = "isGPS";
    public boolean isLatestData;


    public DBHelper(Context context){
         super(context, DATABASE_NAME , null, 1);

    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + BLUETOOTH_TABLE_NAME + " "+
                        "(id integer primary key autoincrement, szerokosc real,dlugosc real,"+
                        "data text, kierunek real,predkosc real,cisnienie real, temperatura real,"+
                        "ax integer, ay integer, az integer, gx integer, gy integer, gz integer,"+
                        "napiecie real, isGPS text)"
        );
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + BLUETOOTH_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(BluetoothDataParser bluetoothDataParser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("szerokosc", String.valueOf(bluetoothDataParser.szerokoscGeograficzna));
        contentValues.put("dlugosc", String.valueOf(bluetoothDataParser.dlugoscGeograficzna));
        contentValues.put("data", String.valueOf(bluetoothDataParser.dateFromGPS));
        contentValues.put("kierunek", String.valueOf(bluetoothDataParser.kierunek));
        contentValues.put("predkosc", String.valueOf(bluetoothDataParser.predkosc));
        contentValues.put("cisnienie", String.valueOf(bluetoothDataParser.cisnienie));
        contentValues.put("temperatura", String.valueOf(bluetoothDataParser.temperatura));
        contentValues.put("ax", String.valueOf(bluetoothDataParser.ax));
        contentValues.put("ay", String.valueOf(bluetoothDataParser.ay));
        contentValues.put("az", String.valueOf(bluetoothDataParser.az));
        contentValues.put("gx", String.valueOf(bluetoothDataParser.gx));
        contentValues.put("gy", String.valueOf(bluetoothDataParser.gy));
        contentValues.put("gz", String.valueOf(bluetoothDataParser.gz));
        contentValues.put("napiecie", String.valueOf(bluetoothDataParser.napiecie));
        contentValues.put("isGPS", bluetoothDataParser.isGPS);
        db.insert(BLUETOOTH_TABLE_NAME, null, contentValues);
        return true;

    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, BLUETOOTH_TABLE_NAME);
        return numRows;
    }
    public ArrayList<String> getAllData() {
        ArrayList<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + BLUETOOTH_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_DATA)));
            res.moveToNext();
        }
        return array_list;
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Data where id="+id+"", null );

        return res;
    }
    public ArrayList<String> getDataAsArray() {
        ArrayList<String> array_list = new ArrayList<>();
        ArrayList<String> rowBuffer = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + BLUETOOTH_TABLE_NAME +
                                   " order by id desc" +
                                   " limit 3600", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            rowBuffer.add("DATA:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_DATA)));
            rowBuffer.add("SZ:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_SZEROKOSCGEOGRAFICZNA)));
            rowBuffer.add("DL:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_DLUGOSCGEOGRAFICZNA)));
            rowBuffer.add("GPS?:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_IS_GPS)));
            rowBuffer.add("KIER:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_KIERUNEK)));
            rowBuffer.add("PRED:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_PREDKOSC)));
            rowBuffer.add("CIS:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_CISNIENIE)));
            rowBuffer.add("TEMP:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_TEMPERATURA)));
            rowBuffer.add("AX:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_AX)));
            rowBuffer.add("AY:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_AY)));
            rowBuffer.add("AZ:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_AZ)));
            rowBuffer.add("GX:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_GX)));
            rowBuffer.add("GY:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_GY)));
            rowBuffer.add("GZ:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_GZ)));
            rowBuffer.add("NAP:"+res.getString(res.getColumnIndex(BLUETOOTH_COLUMN_NAPIECIE)));

            array_list.add(String.valueOf(rowBuffer));
            rowBuffer.clear();

            res.moveToNext();
        }
        return array_list;
    }
    public Cursor getLatestData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * \n" +
                "    FROM    Data\n" +
                "    WHERE   id = (SELECT MAX(id)  FROM Data)", null );
        isLatestData = true;
        return res;
    }

    public Cursor getLatestId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT id \n" +
                "    FROM    Data\n" +
                "    WHERE   id = (SELECT MAX(id)  FROM Data)", null );
        return res;
    }
    public void deleteData(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from "+ BLUETOOTH_TABLE_NAME + " where id!= " +
        "( select min(id) from " + BLUETOOTH_TABLE_NAME + " ) ");
    }
}