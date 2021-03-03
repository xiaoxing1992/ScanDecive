package com.motoband.scandecive

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cn.bingoogolapple.qrcode.core.BarcodeType
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.*
import com.lxj.xpopup.XPopup
import com.motoband.scandecive.network.ApiService
import com.motoband.scandecive.network.MBResponse
import com.motoband.scandecive.network.RetrofitHelper
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_scan_device.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/**
 * @Author:         RenZhengWei
 * @CreateDate:     2020/12/2 15:20
 * @Description:     java类作用描述
 */
class ScanDeviceActivity : AppCompatActivity(), QRCodeView.Delegate {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_device)
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)

        zxing_view.setDelegate(this)
        zxing_view.setType(BarcodeType.ONLY_QR_CODE, null)
        zxing_view.scanBoxView.rectWidth = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(120f)
        zxing_view.scanBoxView.rectHeight = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(120f)
        btSumbit?.setOnClickListener {
            if (imei.isEmpty() || sn.isEmpty()) {
                ToastUtils.showShort("数据为空请重新扫描")
                return@setOnClickListener
            }

            val viewload = XPopup.Builder(this)
                .asLoading("提交中")
            viewload.show()

            val extraParamMap: MutableMap<String, Any> = HashMap()
            extraParamMap["imei"] = imei
            extraParamMap["sn4g"] = sn

            val content = JSON.toJSONString(extraParamMap)
            val toRequestBody =
                content.toRequestBody("application/json; charset=utf-8".toMediaType())
            RetrofitHelper.getRetrofit()?.create(ApiService::class.java)
                ?.postSumit(toRequestBody)
                ?.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        viewload.dismiss()

                        val body = response.body()
                        val toString = body?.string()


                        val parseObject = JSON.parseObject(toString, MBResponse::class.java)

                        LogUtils.e("onResponse-> $parseObject")

                        runOnUiThread {
                            when (parseObject.data) {
                                "1" -> {
                                    ToastUtils.showShort("提交成功")
                                }
                                "2" -> {
                                    ToastUtils.showShort("设备已存在")
                                }
                                "0" -> {
                                    ToastUtils.showShort("提交失败")
                                }
                            }

                            imei = ""
                            sn = ""
                            tContent?.text = ""
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        LogUtils.e("onFailure-> $t")
                        viewload.dismiss()
                        runOnUiThread {
                            ToastUtils.showShort("提交失败")
                            imei = ""
                            sn = ""
                            tContent?.text = ""
                        }
                    }
                })
        }

        requestPermission()
    }


    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RxPermissions(this).requestEach(Manifest.permission.CAMERA)
            .subscribe { permission: Permission ->
                when {
                    permission.granted -> {
                        // 这里做个延时，避免卡顿
                        zxing_view?.postDelayed(object : Runnable {
                            override fun run() {
                                startScan()
                            }
                        }, 300)
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        requestPermission()
                    }
                }
            }

    }

    override fun onStart() {
        super.onStart()

        requestPermission()

    }

    override fun onStop() {
        stopScan()
        super.onStop()
    }


    /**
     * 开始扫描
     */
    private fun startScan() {
        // 打开后置摄像头开始预览，但是并未开始识别
        zxing_view?.startCamera()
        // 显示扫描框，并开始识别
        zxing_view?.startSpotAndShowRect()
    }

    /**
     * 停止扫描
     */
    private fun stopScan() {
        zxing_view?.stopSpotAndHiddenRect()
        zxing_view?.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        zxing_view?.onDestroy()
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, ScanDeviceActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var imei = ""
    private var sn = ""
    override fun onScanQRCodeSuccess(result: String?) {
        result?.let {
//             deviceModel = JSON.parseObject(result, DeviceInfo::class.java)
            val itemList = result.split(",")
            for (i in itemList) {
                val split = i.split(":")
                for (j in split) {
                    if (j == "IMEI") {
                        imei = split.last()
                    }

                    if (j == "SN") {
                        sn = split.last()
                    }
                }
            }
            LogUtils.e("onScanQRCodeSuccess-> $imei  $sn")
            tContent?.text = "IMEI:${imei}\nSN:${sn}"

            zxing_view?.startSpot()// 开始识别
        }
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
    }

    override fun onScanQRCodeOpenCameraError() {
        ToastUtils.showLong("打开相机出错")
    }
}