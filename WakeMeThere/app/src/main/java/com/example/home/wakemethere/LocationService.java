package com.example.home.wakemethere;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import static com.example.home.wakemethere.App.channel_1_id;

public class LocationService extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = "com.example.home.wakemethere.UPDATE_LOCATION";

    public LocationService() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    if (intent != null){
        final String action = intent.getAction();
        if (ACTION_PROCESS_UPDATE.equals(action)){
            LocationResult result = LocationResult.extractResult(intent);
            if (result != null){
                Location location = result.getLastLocation();
                StringBuilder location_str = new StringBuilder("" + location.getLatitude())
                        .append("/")
                        .append(location.getLongitude());
                try {
                    MainActivity.getInstance().showResultOnScreen(location_str.toString());

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        Notification notification = new NotificationCompat.Builder(context, channel_1_id)
                                .setSmallIcon(R.drawable.crossicon)
                                .setContentTitle(location_str.toString())
                                .setContentText(location_str.toString()).setPriority(NotificationCompat.PRIORITY_MIN).build();
                        managerCompat.notify(1,notification);
                        MainActivity.getInstance().showResultOnScreen("Done");



                }catch (Exception ex){
                    Toast.makeText(context,location_str.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    }

}
