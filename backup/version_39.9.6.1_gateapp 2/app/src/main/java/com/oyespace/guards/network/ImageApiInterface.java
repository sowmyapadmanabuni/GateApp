package com.oyespace.guards.network;

import com.oyespace.guards.request.FingerPrintCreateReq;
import com.oyespace.guards.responce.FingerPrintCreateResp;
import com.oyespace.guards.responce.ResponseVisitorLog;
import com.oyespace.guards.responce.SubscriptionResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by Basavarajesh Koni on 4/2/2018.
 */

public interface ImageApiInterface {

    @Multipart
    @POST("oyeliving/api/V1/association/upload")
    @Headers("X-Champ-APIKey:1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1")
    Call<Object> updateImageProfile(@Part MultipartBody.Part image);


    @GET("Images/AudioRecording.3gp")
    @Headers("X-Champ-APIKey:1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1")
    Call<Object> getAudioFile();

}
