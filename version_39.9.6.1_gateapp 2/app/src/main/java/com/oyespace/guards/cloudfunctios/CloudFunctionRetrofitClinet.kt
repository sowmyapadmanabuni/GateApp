package com.oyespace.guards.cloudfunctios

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.oyespace.guards.com.oyespace.guards.cloudfunctios.CloudFunctionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


/**
 * Created by Kalyan on 10/13/2017.
 */
class CloudFunctionRetrofitClinet {


    companion object {
        val TAG: String = CloudFunctionRetrofitClinet::javaClass.name
        val FCM_BASE_URL = "https://us-central1-oyespace-dc544.cloudfunctions.net/"
        //val FCM_BASE_URL = "https://us-central1-oyespace-b7e2d.cloudfunctions.net/"
//        val BASE_URL="http://apidev.oyespace.com/"

        val instance: CloudFunctionApi by lazy {
            Timber.d(TAG, "init")
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create()

            val builder = Retrofit.Builder()
            val retrofit = builder.baseUrl(FCM_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            retrofit.create(CloudFunctionApi::class.java)
        }

    }
}