package com.example.kuikly_test_project.base

/**
 * iOS平台的PlatformUtils实际实现
 */

/**
 * 获取iOS平台特定的状态栏高度
 */
actual fun getPlatformSpecificStatusBarHeight(): Float {
    // iOS状态栏高度：44pt（普通状态栏）+ 灵动岛高度（如果有）
    // 灵动岛高度：约30-35pt，总高度约74-79pt
    // 考虑到灵动岛的存在，使用更高的值来避免遮挡
    return 74f
}

/**
 * 获取iOS平台特定的平台类型
 */
actual fun getPlatformSpecificPlatform(): Platform {
    return Platform.IOS
}
