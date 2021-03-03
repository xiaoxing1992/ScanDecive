package com.motoband.scandecive.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 2018/4/26.
 * OkHttpHelper
 */
object OkHttpHelper {
    private var okHttpClient: OkHttpClient? = null
    @JvmStatic
    fun getOkHttpClient(): OkHttpClient? {
        if (okHttpClient == null) {
            synchronized(OkHttpHelper::class.java) {
                if (okHttpClient == null) {
                    init()
                }
            }
        }
        return okHttpClient
    }

    private fun init() {
        /**添加header这几步一定要分开写 不然header会无效 别问我为什么
         * 我看了build源码 看返回了一个新的对象 猜想是要一个新的对象来接收
         * 我就只定义了一个新的对象来接受新的Request
         * 后面应该就可以，但是我没确定是否成功 ，然后我就全部都拆开了吧buider对象
         * request的新的对象都分开之后 就能看到成功了。。。。巨大的bug 真是让人头疼
         */
        val builder = OkHttpClient.Builder()
        okHttpClient = builder
            .readTimeout(30000, TimeUnit.MILLISECONDS)
            .writeTimeout(30000, TimeUnit.MILLISECONDS)
            .connectTimeout(30000, TimeUnit.MILLISECONDS)
            .addInterceptor(CommonQueryParamsInterceptor())
            .build()
    }
}