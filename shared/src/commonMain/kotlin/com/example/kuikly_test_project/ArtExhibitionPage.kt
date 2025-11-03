package com.example.kuikly_test_project

import com.example.kuikly_test_project.ExhibitionCenterPage.Companion.TITLE
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.reactive.handler.*
import com.example.kuikly_test_project.base.BasePager
import com.example.kuikly_test_project.base.bridgeModule
import com.example.kuikly_test_project.base.PlatformUtils
@Page("artExhibition", supportInLocal = true)
internal class ArtExhibitionPage : BasePager() {

    override fun viewDidLoad() {
        super.viewDidLoad()
    }

   override fun body(): ViewBuilder {
       return {
        attr {
                backgroundColor(Color.WHITE)
            }


           ArtExhibitionNavBar {
               attr {
                   title = "展览列表"
               }
           }

            // 主要内容区域
            View {
                attr {
                    flex(1f)
                    marginTop(PlatformUtils.getStatusBarHeight() + 10f) // 动态获取状态栏高度+10f，适配不同平台
                    padding(20f)
                }
                
                // 欢迎标题
                Text {
                    attr {
                        text("艺术会展")
                        fontSize(24f)
                        fontWeightSemisolid()
                        color(Color(0xFFAD37FE))
                        textAlignCenter()
                        marginBottom(20f)
                    }
                }

               
                   
                }
            }
        
       
   }
}



// 会展中心导航栏组件
internal class ArtExhibitionNavigationBar : ComposeView<ArtExhibitionNavigationBarAttr, ComposeEvent>() {
    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

    override fun createAttr(): ArtExhibitionNavigationBarAttr {
        return ArtExhibitionNavigationBarAttr()
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
                }

                // 返回按钮 - 放在最前面，确保触摸事件不被拦截
                Image {
                    attr {
                        absolutePosition(
                            top = 12f + getPager().pageData.statusBarHeight,
                            left = 12f
                        )
                        width(44f)  // 增加触摸区域宽度，iOS建议44x44
                        height(44f)  // 增加触摸区域高度，iOS建议44x44
                        src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAASBAMAAAB/WzlGAAAAElBMVEUAAAAAAAAAAAAAAAAAAAAAAADgKxmiAAAABXRSTlMAIN/PELVZAGcAAAAkSURBVAjXYwABQTDJqCQAooSCHUAcVROCHBiFECTMhVoEtRYA6URNVVoEtRYA6UMHzQlOjQIAAAAASUVORK5CYII=")
                    }
                    event {
                        click {
                            // 添加调试日志，确认按钮被点击
                            bridgeModule.log("🔙 返回按钮被点击 - 开始关闭页面")
                            getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                .closePage()
                        }
                    }
                }

                // 导航栏内容 - 放在返回按钮后面，避免遮挡
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
                            backgroundLinearGradient(
                                Direction.TO_BOTTOM,
                                ColorStop(Color(0xFF23D3FD), 0f),
                                ColorStop(Color(0xFFAD37FE), 1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

internal class ArtExhibitionNavigationBarAttr : ComposeAttr() {
    var title: String by observable("")
}

internal fun ViewContainer<*, *>.ArtExhibitionNavBar(init: ArtExhibitionNavigationBar.() -> Unit) {
    addChild(ArtExhibitionNavigationBar(), init)
}
