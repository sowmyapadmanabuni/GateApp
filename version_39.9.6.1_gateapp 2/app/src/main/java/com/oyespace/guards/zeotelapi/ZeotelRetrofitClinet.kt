package com.oyespace.guards.zeotelapi

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import oyespace.guards.cloudfunctios.CloudFunctionApi
import oyespace.guards.cloudfunctios.ZeotelFunctionApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


/**
 * Created by Kalyan on 10/13/2017.
 */
class ZeotelRetrofitClinet {


    companion object {
        val TAG: String = ZeotelRetrofitClinet::javaClass.name
        val ZEOTEL_BASE_URL = "http://ex4.zeotel.com/"
//        val BASE_URL="http://apidev.oyespace.com/"

        val instance: ZeotelFunctionApi by lazy {
            Timber.d(TAG, "init")
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create()

            val builder = Retrofit.Builder()
            val retrofit = builder.baseUrl(ZEOTEL_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            retrofit.create(ZeotelFunctionApi::class.java)
        }

    }
}
