package com.example.kuikly_test_project.base

import com.tencent.kuikly.core.base.BaseObject

/**
 * 平台工具类，用于检测当前运行平台和获取平台特定的配置
 */
internal object PlatformUtils : BaseObject() {
    
    /**
     * 获取状态栏高度（包括导航栏、灵动岛等）
     * 根据不同平台返回不同的高度值
     */
    fun getStatusBarHeight(): Float {
        return getPlatformSpecificStatusBarHeight()
    }
    
    /**
     * 获取当前运行平台
     */
    fun getCurrentPlatform(): Platform {
        return getPlatformSpecificPlatform()
    }
}

/**
 * 平台枚举
 */
enum class Platform {
    ANDROID,
    IOS,
    OHOS,
    UNKNOWN
}

/**
 * 获取平台特定的状态栏高度
 * 使用expectActual机制为不同平台提供不同实现
 */
expect fun getPlatformSpecificStatusBarHeight(): Float

/**
 * 获取平台特定的平台类型
 * 使用expectActual机制为不同平台提供不同实现
 */
expect fun getPlatformSpecificPlatform(): Platform
