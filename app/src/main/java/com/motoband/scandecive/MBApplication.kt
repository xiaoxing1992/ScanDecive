package com.motoband.scandecive

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.ThreadUtils

/**
 * @Author:         RenZhengWei
 * @CreateDate:     2020/12/2 15:42
 * @Description:     java类作用描述
 */
class MBApplication: MultiDexApplication() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (ThreadUtils.isMainThread()) {
            MultiDex.install(this)
        }
    }

}