package com.motoband.scandecive.network

import com.motoband.scandecive.network.OkHttpHelper.getOkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitHelper
 * Created by Administrator on 2017/10/26.
 */
object RetrofitHelper {
    private var retrofit: Retrofit? = null
    fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            synchronized(RetrofitHelper::class.java) {
                if (retrofit == null) {
                    init()
                }
            }
        }
        return retrofit
    }

    private fun init() {
        retrofit = Retrofit.Builder()
            .client(getOkHttpClient()!!)
            .baseUrl("https://api.test.motuobang.com/test/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}