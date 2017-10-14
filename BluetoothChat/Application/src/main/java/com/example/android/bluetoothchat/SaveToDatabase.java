package com.example.android.bluetoothchat;

import com.example.android.common.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mateusz on 14.10.17.
 */

public class SaveToDatabase {
    List<String> listString= new ArrayList<>();
    int i = 0;
    public SaveToDatabase(byte[] buffer) {
         Log.i("NowaKlasa",new String(buffer));
        }

    private void wyswietlPoDziesieciuWywolaniach() {
        for(String string: listString) {
            Log.i("listString", string);
        }
    }
}
