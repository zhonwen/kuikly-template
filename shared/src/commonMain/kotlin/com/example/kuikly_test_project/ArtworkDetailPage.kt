package com.example.kuikly_test_project

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.reactive.handler.*
import com.example.kuikly_test_project.base.BasePager
import com.example.kuikly_test_project.base.bridgeModule
import com.example.kuikly_test_project.base.PlatformUtils
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.views.layout.*
import com.tencent.kuikly.core.views.Scroller

/**
 * 艺术作品详情页
 * 
 * 功能特性：
 * 1. 显示艺术作品的详细信息
 * 2. 支持返回上一页
 * 3. 响应式UI更新
 */
@Page("artworkDetail", supportInLocal = true)
internal class ArtworkDetailPage : BasePager() {
    
    // 作品数据
    private var artworkData by observable<JSONObject?>(null)
    
    override fun viewDidLoad() {
        super.viewDidLoad()
        // 从页面参数中获取作品数据
        artworkData = pagerData.params.optJSONObject("artwork")
        bridgeModule.log("详情页加载，作品数据: ${artworkData?.toString()}")
    }
    
    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
            }
            
            // 导航栏
            ArtworkDetailNavBar {
                attr {
                    title = "作品详情"
                }
            }
            
            // 主要内容区域
            View {
                attr {
                    flex(1f)
                    marginTop(PlatformUtils.getStatusBarHeight() + 64f) // 导航栏高度 + 状态栏高度
                    paddingLeft(20f)
                    paddingRight(20f)
                    paddingTop(20f)
                    paddingBottom(20f)
                }
                
                // 使用Scroller包装内容，支持滚动
                Scroller {
                    attr {
                        width(pagerData.pageViewWidth - 40f)
                        height(pagerData.pageViewHeight - 180f)
                        showScrollerIndicator(true)
                        flexDirectionColumn()
                    }
                    
                    // 作品图片
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 40f)
                            height(300f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(12f)
                            allCenter()
                        }
                        
                        Image {
                            attr {
                                width(pagerData.pageViewWidth - 80f)
                                height(280f)
                                src(ctx.artworkData?.optJSONObject("Cover")?.optString("FilePath", "") ?: "")
                                borderRadius(8f)
                            }
                        }
                    }
                    
                    // 作品信息
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 40f)
                            marginTop(20f)
                        }
                        
                        // 作品名称
                        Text {
                            attr {
                                text(ctx.artworkData?.optString("Name", "未知作品") ?: "未知作品")
                                fontSize(24f)
                                fontWeightSemisolid()
                                color(Color(0xFF333333))
                                marginBottom(16f)
                            }
                        }
                        
                        // 作品材质
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                marginBottom(12f)
                            }
                            
                            Text {
                                attr {
                                    text("材质：")
                                    fontSize(16f)
                                    color(Color(0xFF666666))
                                    marginRight(8f)
                                }
                            }
                            
                            Text {
                                attr {
                                    text(ctx.artworkData?.optString("Material", "未知") ?: "未知")
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                        
                        // 作品描述（如果有的话）
                        val description = ctx.artworkData?.optString("Description", "")
                        if (!description.isNullOrEmpty()) {
                            View {
                                attr {
                                    marginTop(16f)
                                }
                                
                                Text {
                                    attr {
                                        text("描述：")
                                        fontSize(16f)
                                        color(Color(0xFF666666))
                                        marginBottom(8f)
                                    }
                                }
                                
                                Text {
                                    attr {
                                        text(description)
                                        fontSize(16f)
                                        color(Color(0xFF333333))
                                        lineHeight(24f)
                                    }
                                }
                            }
                        }
                        
                        // 其他作品信息（根据实际数据结构添加）
                        ctx.artworkData?.let { artwork: JSONObject ->
                            // 可以添加更多字段，比如：
                            // - 创作年份
                            // - 艺术家
                            // - 尺寸
                            // - 价格等
                        }
                    }
                }
            }
        }
    }
    
    companion object {
        const val TITLE = "作品详情"
    }
}

// 作品详情页导航栏组件
internal class ArtworkDetailNavigationBar : ComposeView<ArtworkDetailNavigationBarAttr, ComposeEvent>() {
    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

    override fun createAttr(): ArtworkDetailNavigationBarAttr {
        return ArtworkDetailNavigationBarAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    absolutePosition(top = 0f, left = 0f, right = 0f)
                    height(64f)
                    paddingTop(ctx.pagerData.statusBarHeight)
                    backgroundColor(Color.WHITE)
                    zIndex(999)  // 确保在最上层
                }
                
                // 导航栏内容
                View {
                    attr {
                        height(44f)
                        allCenter()
                    }

                    Text {
                        attr {
                            text(ctx.attr.title)
                            fontSize(17f)
                            fontWeightSemisolid()
                            color(Color(0xFF333333))
                        }
                    }
                }

                // 返回按钮
                Image {
                    attr {
                        absolutePosition(
                            top = 12f + getPager().pageData.statusBarHeight,
                            left = 12f,
                            bottom = 12f,
                            right = 12f
                        )
                        size(10f, 17f)
                        zIndex(999)  // 添加高层级，确保在最上层
                        src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAASBAMAAAB/WzlGAAAAElBMVEUAAAAAAAAAAAAAAAAAAAAAAADgKxmiAAAABXRSTlMAIN/PELVZAGcAAAAkSURBVAjXYwABQTDJqCQAooSCHUAcVROCHBiFECTMhVoEtRYA6UMHzQlOjQIAAAAASUVORK5CYII=")
                    }
                    event {
                        click {
                            // 添加调试日志，确认按钮被点击
                            bridgeModule.log("🔙 详情页返回按钮被点击 - 开始关闭页面")
                            getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                .closePage()
                        }
                    }
                }
            }
        }
    }
}

internal class ArtworkDetailNavigationBarAttr : ComposeAttr() {
    var title: String by observable("")
}

internal fun ViewContainer<*, *>.ArtworkDetailNavBar(init: ArtworkDetailNavigationBar.() -> Unit) {
    addChild(ArtworkDetailNavigationBar(), init)
}
