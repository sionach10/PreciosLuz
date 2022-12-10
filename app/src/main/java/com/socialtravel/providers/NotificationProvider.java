package com.socialtravel.providers;

import com.socialtravel.models.FCMBody;
import com.socialtravel.models.FCMResponse;
import com.socialtravel.retrofit.IFCMApi;
import com.socialtravel.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
