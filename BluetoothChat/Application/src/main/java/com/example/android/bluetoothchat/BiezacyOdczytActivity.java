package com.example.android.bluetoothchat;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class BiezacyOdczytActivity extends Activity {
    BluetoothDataParser bluetoothDataParser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biezacyodczyt);

        bluetoothDataParser = BluetoothDataParser.getInstance();
        final Button button = (Button) findViewById(R.id.button_ok);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // bluetoothDataParser.wyswietlBufor();
            }
        });
    }

}
