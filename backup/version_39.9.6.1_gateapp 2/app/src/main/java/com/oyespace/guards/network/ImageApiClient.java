package com.oyespace.guards.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.oyespace.guards.utils.ConstantUtils.IMAGE_BASE_URL;

/**
 * Created by Basavarajesh Koni on 4/2/2018.
 */

public class ImageApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getImageClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(IMAGE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}