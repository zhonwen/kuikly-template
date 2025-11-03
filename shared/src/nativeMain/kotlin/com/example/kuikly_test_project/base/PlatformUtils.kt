package com.example.kuikly_test_project.base

import com.example.kuikly_test_project.base.Platform

/**
 * Native平台特定的实现
 */
actual fun getPlatformSpecificStatusBarHeight(): Float {
    // Native平台默认状态栏高度
    return 24f
}

/**
 * Native平台特定的平台类型
 */
actual fun getPlatformSpecificPlatform(): Platform {
    return Platform.UNKNOWN
}