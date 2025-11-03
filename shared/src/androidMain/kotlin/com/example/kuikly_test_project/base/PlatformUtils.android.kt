package com.example.kuikly_test_project.base

/**
 * Android平台的PlatformUtils实际实现
 */

/**
 * 获取Android平台特定的状态栏高度
 */
actual fun getPlatformSpecificStatusBarHeight(): Float {
    // Android状态栏高度：24dp（标准）+ 导航栏高度（如果有）
    // 转换为像素：24 * 3 = 72px (3x设备)
    // 考虑到不同设备的差异，使用64f作为安全值
    return 64f
}

/**
 * 获取Android平台特定的平台类型
 */
actual fun getPlatformSpecificPlatform(): Platform {
    return Platform.ANDROID
}

