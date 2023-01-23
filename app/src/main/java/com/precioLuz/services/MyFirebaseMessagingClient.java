package com.precioLuz.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.precioLuz.R;
import com.precioLuz.channel.NotificationHelper;
import com.precioLuz.models.Message;
import com.precioLuz.receivers.MessageReceiver;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    public static final String NOTIFICATION_REPLY = "NotificationReply";


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

        String imageSender = data.get("imageSender");
        String imageReceiver = data.get("imageReceiver");

        if(imageSender == null) {
            imageSender="IMAGEN NO VALIDA";//Ponemos algo para que sea distinto de null y no de crash el Picasso.load().
        }
        if(imageReceiver == null) {
            imageReceiver="IMAGEN NO VALIDA";
        }

        getImageSender(data, imageSender, imageReceiver);
    }


    //Me esta crasheando cuando imageSender es null. Sin embargo, cuando debugueo, entra por onBitmapFailed (donde debería entrar)
    // imagino que porque va más despacio y le da tiempo. Voy a controlarla fuera de esta función para que no de crash.

    private void getImageSender(Map<String, String> data, String imageSender, String imageReceiver) {
        new Handler(Looper.getMainLooper()) //Cargar la imagen del usuario en la notificación.
                .post(new Runnable() {
                    @Override
                    public void run() {
                        //if(imageSender!= null) {
                            Picasso.with(getApplicationContext())
                                    .load(imageSender)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {//Si no hay imagen no entra en este método.
                                            getImageReceiver(data, imageReceiver, bitmapSender);

                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                            getImageReceiver(data, imageReceiver, null);
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        //}
                        //else {
                        //    getImageReceiver(data, imageReceiver, null);
                        //}

                    }
                });
    }

    private void getImageReceiver(Map<String, String> data, String imageReceiver, Bitmap bitmapSender) {
        //if(imageReceiver != null) {
            Picasso.with(getApplicationContext())
                    .load(imageReceiver)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {
                            notifyMessage(data, bitmapSender, bitmapReceiver);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) { //Entra aquí si no hay imagen.
                            notifyMessage(data, bitmapSender, null);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        //}
        //else {
        //    notifyMessage(data, bitmapSender, null);
        //}

    }

    private void notifyMessage(Map<String, String> data, Bitmap bitmapSender, Bitmap bitmapReceiver) {
        final String usernameSender = data.get("usernameSender");
        final String usernameReceiver = data.get("usernameReceiver");
        final String lastMessage = data.get("lastMessage");
        String messagesJSON = data.get("messages");
        final String imageSender = data.get("imageSender");
        final String imageReceiver = data.get("imageReceiver");

        final String idSender = data.get("idSender");
        final String idReceiver = data.get("idReceiver");
        final String idChat = data.get("idChat");
        final int idNotification = Integer.parseInt(data.get("idNotification"));

        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra("idSender", idSender);
        intent.putExtra("idReceiver", idReceiver);
        intent.putExtra("idChat", idChat);
        intent.putExtra("idNotification", idNotification);
        intent.putExtra("usernameSender", usernameSender);
        intent.putExtra("usernameReceiver", usernameReceiver);
        intent.putExtra("imageSender", imageSender);
        intent.putExtra("imageReceiver", imageReceiver);



        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationMessages(
                messages,
                usernameSender,
                usernameReceiver,
                lastMessage,
                bitmapSender,
                bitmapReceiver,
                action);
        notificationHelper.getManager().notify(idNotification, builder.build());//Con el id: 1 solo se manda una notificación y cuando se mande una nueva, como todas tienen id:1 se sustituye por el último mensaje enviado.
    }
}
