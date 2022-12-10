package com.socialtravel.retrofit;

import com.socialtravel.models.FCMBody;
import com.socialtravel.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type: application/json",
            "Autorization:key=AAAApG7ECCg:APA91bFf83pvulZcLlmIknFm20sX3EOK7iPUt7GDByNkE46iqcgz8bSIEzWN32DddmyYlihHMfaxDp05YqPQ93hACIRXWBMoz89F9ZFMWrXuAMI5ZCdskbcL71qZtRCdlmDrw5h5EUPX"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
