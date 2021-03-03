package com.motoband.scandecive.network

import com.blankj.utilcode.util.EncodeUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * @Author:         RenZhengWei
 * @CreateDate:     2020/7/10 11:16
 * @Description:     java类作用描述
 */
/**
 * 设置项目的请求头加密参数
 */
class CommonQueryParamsInterceptor : Interceptor {

    private val HMAC_ALGORITHM = "HmacSHA1"
    private val CHARSET_UTF_8 = "UTF-8"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Source", "source")
            .addHeader("Date", getHeaderDate())
            .addHeader("Authorization", getHeaderAuthen()!!)
            .method(originalRequest.method, originalRequest.body)

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }


    private fun getHeaderAuthen(): String? {
        try {
            val sig: String? = sign(API.APP_SECRETKEY, getHeaderDate())
            val authen = ("hmac id=\""
                    + API.APP_SECRETID
                    ) + "\", algorithm=\"hmac-sha1\", headers=\"date source\", signature=\"" + sig + "\""
            return authen
        } catch (ex: Exception) {
            return null
            //      Timber.e(ex);
        }
    }

    @Throws(
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class,
        InvalidKeyException::class
    )
    private fun sign(secret: String, timeStr: String): String? {
        //get signStr
        val signStr = "date: $timeStr\nsource: source"
        //get sig
        var sig: String? = null
        val mac1 = Mac.getInstance(HMAC_ALGORITHM)
        val hash: ByteArray
        val secretKey =
            SecretKeySpec(secret.toByteArray(charset(CHARSET_UTF_8)), mac1.algorithm)
        mac1.init(secretKey)
        hash = mac1.doFinal(signStr.toByteArray(charset(CHARSET_UTF_8)))
        sig = String(EncodeUtils.base64Encode(hash))
        println("signValue--->$sig")
        return sig
    }

    private fun getHeaderDate(): String {
        // get current GMT time
        // get current GMT time
        val cd = Calendar.getInstance()
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val timeStr = sdf.format(cd.time)
        return timeStr
    }
}
