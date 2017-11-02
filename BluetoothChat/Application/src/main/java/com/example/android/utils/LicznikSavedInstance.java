package com.example.android.utils;

/**
 * @author Mateusz Trzeciak
 */

public class LicznikSavedInstance {
    private static LicznikSavedInstance instance = null;
    public Float predkoscMax = (float) 0;
    public Float przejechanyDystansWKilometrach = (float) 0;
    public long czasJazdyWSekundach = 0;
    public int idStartu = 0;
    public boolean tripIsStopped = false;
    public boolean tripIsEnabled = false;

    public static LicznikSavedInstance getInstance (){
        if(instance == null){
            instance = new LicznikSavedInstance();
        }
        return instance;
    }

}
