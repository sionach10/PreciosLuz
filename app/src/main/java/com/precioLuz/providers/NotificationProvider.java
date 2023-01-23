package com.precioLuz.providers;

import com.precioLuz.models.FCMBody;
import com.precioLuz.models.FCMResponse;
import com.precioLuz.retrofit.IFCMApi;
import com.precioLuz.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
