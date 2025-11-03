# PlatformUtils 平台工具类

## 概述

`PlatformUtils` 是一个跨平台工具类，用于解决不同平台（Android、iOS、OpenHarmony）之间的差异，特别是状态栏高度适配问题。

## 问题背景

在跨平台开发中，不同平台的状态栏高度存在差异：
- **Android**: 标准状态栏高度约64px
- **iOS**: 状态栏高度约44pt，加上灵动岛（Dynamic Island）后约74pt
- **OpenHarmony**: 类似Android，约64px

## 解决方案

使用Kotlin Multiplatform的 `expectActual` 机制：

### 1. 通用接口（commonMain）
```kotlin
// 声明期望函数
expect fun getPlatformSpecificStatusBarHeight(): Float
expect fun getPlatformSpecificPlatform(): Platform
```

### 2. 平台特定实现
- **Android**: `shared/src/androidMain/kotlin/.../PlatformUtils.android.kt`
- **iOS**: `shared/src/iosMain/kotlin/.../PlatformUtils.ios.kt`

### 3. 使用方式
```kotlin
// 获取动态状态栏高度
val statusBarHeight = PlatformUtils.getStatusBarHeight()

// 在UI中使用
View {
    attr {
        marginTop(PlatformUtils.getStatusBarHeight())
    }
}
```

## 优势

1. **编译时确定**: 在编译时确定平台，运行时无性能损耗
2. **类型安全**: 编译时检查确保所有平台都有实现
3. **易于维护**: 平台特定代码分离，便于维护
4. **自动适配**: 无需手动判断平台，自动使用正确的值

## 扩展

可以轻松添加更多平台特定的配置：
- 字体大小
- 颜色主题
- 布局间距
- 动画时长

## 注意事项

1. 确保所有平台都有对应的 `actual` 实现
2. 编译时检查会确保实现完整性
3. 新增平台时需要添加对应的实现文件
