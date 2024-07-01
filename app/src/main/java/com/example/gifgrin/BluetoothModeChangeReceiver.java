package com.example.gifgrin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class BluetoothModeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            if (isBluetoothActive(context) == true) {
                Toast.makeText(context,"Bluetooth ON",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"Bluetooth OFF",Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isBluetoothActive(Context context) {

            return Settings.System.getInt(context.getContentResolver(),
                    Settings.Global.BLUETOOTH_ON, 0) != 0;
    }
}
