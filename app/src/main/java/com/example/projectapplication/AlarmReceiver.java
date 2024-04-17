package com.example.projectapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String plantName = intent.getStringExtra("plantName");
        String plantPlace = intent.getStringExtra("plantPlace");
        Uri plantImageUri = intent.getParcelableExtra("plantImageUri");

        NotificationHelper.showNotification(context, plantName, plantPlace, plantImageUri);
    }
}

