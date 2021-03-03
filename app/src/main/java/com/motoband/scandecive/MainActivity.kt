package com.motoband.scandecive

import android.Manifest.permission
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btGo?.setOnClickListener {
            RxPermissions(this).requestEach(permission.CAMERA)
                    .subscribe { permission: Permission ->
                        when {
                            permission.granted -> {
                                ScanDeviceActivity.start(this)
                            }
                            permission.shouldShowRequestPermissionRationale -> {
                                ToastUtils.showLong("拒绝相机权限")
                            }
                        }
                    }

        }
    }
}