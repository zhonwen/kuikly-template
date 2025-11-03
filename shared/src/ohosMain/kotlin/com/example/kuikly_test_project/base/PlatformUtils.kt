package com.example.kuikly_test_project.base

import com.example.kuikly_test_project.base.Platform

/**
 * OHOS平台特定的实现
 */
actual fun getPlatformSpecificStatusBarHeight(): Float {
    // OHOS平台默认状态栏高度
    return 24f
}

/**
 * OHOS平台特定的平台类型
 */
actual fun getPlatformSpecificPlatform(): Platform {
    return Platform.OHOS
}