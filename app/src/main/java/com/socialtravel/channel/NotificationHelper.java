package com.socialtravel.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.socialtravel.R;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.socialtravel";
    private static final String CHANNEL_NAME = "socialtravel";
    private NotificationManager manager; //Normalmente en java, el poner la variable mTipo ejm: mNotificationManager se hace en las actividades, no en las clases.

    public NotificationHelper(Context context) {
        super(context);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)//Solo disponible en la version Android superior o igual a Oreo (Api 26).
    private void createChannels() {
        NotificationChannel notificationChannel =
                new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if(manager==null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotification(String title, String body) {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
}
