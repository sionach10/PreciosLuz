package com.socialtravel.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.PeriodicSync;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.socialtravel.R;
import com.socialtravel.activities.ChatActivity;
import com.socialtravel.activities.HomeActivity;
import com.socialtravel.fragments.ChatsFragment;
import com.socialtravel.models.Message;

import java.util.Date;

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

        //Cuando pulsamos clic en la notificación nos lleva a la pantalla de HomeActivity.
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    public NotificationCompat.Builder getNotificationMessages(Message[] messages,
                                                                String usernameSender,
                                                                String usernameReceiver,
                                                                String lastMessage,
                                                                Bitmap bitmapSender,
                                                                Bitmap bitmapReceiver,
                                                                NotificationCompat.Action action) {

        Person person1 = null;
        Person person2 = null;

        if(bitmapReceiver == null) { //Si es nulo, imagen por defecto, si tiene datos, cargamos los datos de la imagen.
            person1 = new Person.Builder()
                    .setName(usernameReceiver)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_person))
                    .build();
        }
        else {
            person1 = new Person.Builder()
                    .setName(usernameReceiver)
                    .setIcon(IconCompat.createWithBitmap(bitmapReceiver))
                    .build();
        }

        if(bitmapSender == null) {
            person2 = new Person.Builder()
                    .setName(usernameSender)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_person))
                    .build();
        }
        else {
            person2 = new Person.Builder()
                    .setName(usernameSender)
                    .setIcon(IconCompat.createWithBitmap(bitmapSender))
                    .build();
        }

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person1);
        NotificationCompat.MessagingStyle.Message message1 = new NotificationCompat.MessagingStyle.Message(lastMessage, new Date().getTime(), person1);

        messagingStyle.addMessage(message1);

        for (Message m: messages) {
            NotificationCompat.MessagingStyle.Message message2 = new NotificationCompat.MessagingStyle.Message(m.getMessage(), m.getTimestamp(), person2);
            messagingStyle.addMessage(message2);
        }

        //Cuando pulsamos clic en la notificación nos lleva a la pantalla de HomeActivity. No puedo poner ChatActivity porque me crashea porque le faltan muchos datos y me da punteros nulos.
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(messagingStyle)
                .addAction(action)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                ;
    }
}
