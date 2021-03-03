package com.motoband.scandecive.network

/**
 * @Author:         RenZhengWei
 * @CreateDate:     2020/12/2 17:28
 * @Description:     java类作用描述
 */
data class MBResponse(
    val code: String = "",            // 服务器返回码
    val data: String = "",                  // 服务器返回数据
    val msg: String = "",                  // 服务器返回数据
    val sign: String = ""            // sign签名信息
)