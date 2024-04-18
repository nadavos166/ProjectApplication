package com.example.projectapplication;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String plantName = intent.getStringExtra("plantName");
        String plantPlace = intent.getStringExtra("plantPlace");
        Uri plantImageUri = intent.getParcelableExtra("plantImageUri");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle(plantName)
                        .setContentText("The place is " + plantPlace)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            return;
        }
       notificationManagerCompat.notify(1, builder.build());
    }
}

