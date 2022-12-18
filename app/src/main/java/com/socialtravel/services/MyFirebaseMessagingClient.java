package com.socialtravel.services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.socialtravel.channel.NotificationHelper;
import com.socialtravel.models.Message;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        if(title != null) {
            if(title.equals("NUEVO MENSAJE")) {

                showNotificationMessage(data);
            }
            else {
                showNotification(title, body);
            }
        }
    }

    private void showNotification(String title, String body) {
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title, body);
        Random random = new Random(); //vamos a generar id randoms.
        int n = random.nextInt(10000); //Num aleatorio entre 0 y 10.000.
        notificationHelper.getManager().notify(n, builder.build());//Con el id: 1 solo se manda una notificación y cuando se mande una nueva, como todas tienen id:1 se sustituye por el último mensaje enviado.

    }

    private void showNotificationMessage(Map<String, String> data) {
        String tittle = data.get("tittle");
        String body = data.get("body");
        String usernameSender = data.get("usernameSender");
        String usernameReceiver = data.get("usernameReceiver");
        String lastMessage = data.get("lastMessage");
        String messagesJSON = data.get("messages");
        int idNotification = Integer.parseInt(data.get("idNotification"));
        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationMessages(messages, usernameSender, usernameReceiver, lastMessage);
        notificationHelper.getManager().notify(idNotification, builder.build());//Con el id: 1 solo se manda una notificación y cuando se mande una nueva, como todas tienen id:1 se sustituye por el último mensaje enviado.

    }
}
