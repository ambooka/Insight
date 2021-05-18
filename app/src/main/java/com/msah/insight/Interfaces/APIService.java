package com.msah.insight.Interfaces;

import com.msah.insight.notifications.MyResponse;
import com.msah.insight.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

//TODO: Authorization key
public interface APIService {
    @Headers(
            {
                        "Content-Type:application/json",
                        "Authorization:key=AAAASsxLNd8:APA91bEfbtZURKrWPmFIF46VydBmEo6k4i5M50_hGMjl8aI2rbRnfjdODBfhZapb8JtzKNx1X_nWsY0OqGq2qPvcRiy1SzOBVT2aGFBbVxESbBWojcKURKCrKwxLNq1OjxW93Ey4-p8e",
           }
            )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
