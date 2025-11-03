// 使用命名对象包裹常量，避免顶级作用域解析问题
object Constants {
    const val KEY_PAGE_NAME = "pageName" // 页面名称的键（用于KSP注解处理器传递参数）
}

plugins {
    kotlin("multiplatform")
    // 暂时禁用Cocoapods插件（原因：当前仅开发Android端，iOS功能待后续开发）
    // kotlin("native.cocoapods")
    id("com.android.library")
    id("com.google.devtools.ksp") // KSP用于注解处理
    id("maven-publish") // 用于发布模块到Maven仓库
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8" // 指定JVM目标版本为1.8，兼容低版本Android
            }
        }
        publishLibraryVariants("release") // 仅发布release变体到仓库
    }

    // 暂时注释iOS相关目标平台（原因：当前阶段专注Android端开发）
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()
//
//    cocoapods {
//        summary = "Shared module for MyApplication (包含业务逻辑和数据模型)"
//        homepage = "https://example.com/shared-module"
//        version = "1.0"
//        ios.deploymentTarget = "14.1" // 最低支持iOS 14.1
//        podfile = project.file("../iosApp/Podfile") // 关联iOS项目的Podfile
//        framework {
//            baseName = "shared" // 生成的Framework名称
//            freeCompilerArgs = freeCompilerArgs + getCommonCompilerArgs()
//            isStatic = true // 静态链接Framework
//            license = "MIT"
//        }
//        extraSpecAttributes["resources"] = "['src/commonMain/assets/**']" // 指定资源路径
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // 引入Kuikly核心库（跨平台基础框架）
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyVersion()}")
            }
        }

        val androidMain by getting {
            dependencies {
                // 引入Kuikly Android渲染库（仅Android端需要）
                api("com.tencent.kuikly-open:core-render-android:${Version.getKuiklyVersion()}")
            }
        }

        // 暂时注释iOS相关源码集配置
        // val iosX64Main by getting
        // val iosArm64Main by getting
        // val iosSimulatorArm64Main by getting
        // val iosMain by creating {
        //     dependsOn(commonMain)
        //     iosX64Main.dependsOn(this)
        //     iosArm64Main.dependsOn(this)
        //     iosSimulatorArm64Main.dependsOn(this)
        // }
    }
}

// 模块坐标和版本（版本优先从环境变量获取，默认1.0.0）
group = "com.example.myapplication"
version = System.getenv("kuiklyBizVersion") ?: "1.0.0"

publishing {
    repositories {
        // 暂时注释Maven发布配置（原因：当前未启用远程仓库发布，待后续配置）
        // maven {
        //     credentials {
        //         username = System.getenv("mavenUserName") ?: "" // 从环境变量获取用户名
        //         password = System.getenv("mavenPassword") ?: "" // 从环境变量获取密码
        //     }
        //     url = uri(rootProject.properties["mavenUrl"] as? String ?: "") // 仓库地址从根项目属性获取
        // }
    }
}

ksp {
    // 注意：这里需要通过Constants对象访问常量
    arg(Constants.KEY_PAGE_NAME, getPageName())
}

dependencies {
    // 引入Kuikly的KSP处理器（仅编译时依赖）
    compileOnly("com.tencent.kuikly-open:core-ksp:${Version.getKuiklyVersion()}") {
        add("kspAndroid", this) // 为Android平台添加KSP依赖
        // 暂时注释iOS平台的KSP依赖（与iOS目标平台一起禁用）
        // add("kspIosArm64", this)
        // add("kspIosX64", this)
        // add("kspIosSimulatorArm64", this)
    }
}

android {
    namespace = "com.example.myapplication.shared"
    compileSdk = 34 // 编译SDK版本
    defaultConfig {
        minSdk = 21 // 最低支持Android 5.0
        targetSdk = 30 // 目标SDK版本
    }
    sourceSets {
        named("main") {
            // 将commonMain的assets目录关联到Android的assets（共享资源）
            assets.srcDirs("src/commonMain/assets")
        }
    }
}

/**
 * 获取页面名称（优先从项目属性获取，默认空字符串）
 * 用于KSP生成代码时标记页面标识
 */
fun getPageName(): String {
    // 注意：这里需要通过Constants对象访问常量
    return (project.properties[Constants.KEY_PAGE_NAME] as? String) ?: ""
}

/**
 * 获取通用的Kotlin编译器参数
 * 目前指定-Xallocator=std：使用标准内存分配器（适用于调试和兼容性场景）
 */
fun getCommonCompilerArgs(): List<String> {
    return listOf(
        "-Xallocator=std"
    )
}

/**
 * 获取链接器参数（目前未使用，预留扩展）
 */
fun getLinkerArgs(): List<String> {
    return listOf()
}
    