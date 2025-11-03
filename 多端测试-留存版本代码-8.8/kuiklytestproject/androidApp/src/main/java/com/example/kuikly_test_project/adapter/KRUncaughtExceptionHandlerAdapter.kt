package com.example.kuikly_test_project.adapter

import android.util.Log
import com.tencent.kuikly.core.render.android.adapter.IKRUncaughtExceptionHandlerAdapter
// 使用完整包名引用BuildConfig
import com.example.kuikly_test_project.BuildConfig

object KRUncaughtExceptionHandlerAdapter : IKRUncaughtExceptionHandlerAdapter {

    private const val TAG = "KRExceptionHandler"

    override fun uncaughtException(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            throw throwable
        } else {
            Log.e(TAG, "KR error: ${throwable.stackTraceToString()}")
        }
    }

}