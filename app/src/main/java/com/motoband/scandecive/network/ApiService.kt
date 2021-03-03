package com.motoband.scandecive.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * @Author:         RenZhengWei
 * @CreateDate:     2020/12/2 16:30
 * @Description:     java类作用描述
 */
interface ApiService {
    @POST("operate/gps/scancode")
    fun postSumit(@Body body: RequestBody): Call<ResponseBody>

}