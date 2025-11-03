package com.example.kuikly_test_project

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.utils.urlParams
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.reactive.handler.*
import com.example.kuikly_test_project.base.BasePager
import com.example.kuikly_test_project.base.bridgeModule

@Page("router", supportInLocal = true)
internal class RouterPage : BasePager() {

    var inputText: String = ""
    lateinit var inputRef: ViewRef<InputView>

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
            }
            // 背景图
            RouterNavBar {
                attr {
                    title = TITLE
//                    backDisable = true
                }
            }

            View {
                attr {
                    allCenter()
                    margin(20f)
                }
                View {
                    attr {
                        backgroundColor(Color.WHITE)
                        borderRadius(10f)
                        padding(10f)
                    }
                    Image {
                        attr {
                            src(LOGO)
                            size(
                                pagerData.pageViewWidth * 0.4f,
                                (pagerData.pageViewWidth * 0.4f) * (1678f / 2284f)
                            )
                        }
                    }
                }

            }

            View {
                attr {
                    flexDirectionRow()
                }
                View {
                    attr {
                        margin(all = 10f)
                        marginTop(0f)
                        height(40f)
                        flex(1f)
                        borderRadius(5f)
                    }
                    View {
                        attr {
                            absolutePositionAllZero()
                            backgroundLinearGradient(
                                Direction.TO_LEFT,
                                ColorStop(Color(0xFF23D3FD), 0f),
                                ColorStop(Color(0xFFAD37FE), 1f)
                            )
                        }
                        View {
                            attr {
                                absolutePosition(top = 1f, left = 1f, right = 1f, bottom = 1f)
                                backgroundColor(Color.WHITE)
                                borderRadius(5f)
                            }
                        }
                    }
                    Input {
                        ref {
                            ctx.inputRef = it
                        }
                        attr {
                            flex(1f)
                            fontSize(15f)
                            color(Color(0xFFAD37FE))
                            marginLeft(10f)
                            marginRight(10f)
                            placeholder(PLACEHOLDER)
                            autofocus(true)
                            placeholderColor(Color(0xAA23D3FD))

                        }
                        event {
                            textDidChange {
                                ctx.inputText = it.text
                            }
                        }
                    }
                }
                Button {
                    attr {
                        size(80f, 40f)
                        borderRadius(20f)
                        marginLeft(2f)
                        marginRight(15f)
                        backgroundLinearGradient(
                            Direction.TO_BOTTOM,
                            ColorStop(Color(0xAA23D3FD), 0f),
                            ColorStop(Color(0xAAAD37FE), 1f)
                        )

                        titleAttr {
                            text(JUMP_TEXT)
                            fontSize(17f)
                            color(Color.WHITE)
                        }
                    }
                    event {
                        click {
                            if (ctx.inputText.isEmpty()) {
                                // 请输入PageName
                            } else {
//
//                                ctx.inputRef.view?.blur() // 失焦
//                                getPager().acquireModule<SharedPreferencesModule>(
//                                    SharedPreferencesModule.MODULE_NAME
//                                ).setItem(
//                                    CACHE_KEY, ctx.inputText
//                                )
//                                ctx.jumpPage(ctx.inputText)
                                ctx.jumpToExhibitionCenter()
                            }
                        }
                    }
                }

            }

            Text {
                attr {
                    fontSize(15f)
                    marginLeft(10f)
                    marginTop(5f)
                    text(if (pagerData.params.optString("execute_mode") == "1") AAR_MODE_TIP else TIP)

                    backgroundLinearGradient(
                        Direction.TO_RIGHT,
                        ColorStop(Color(0xFFAD37FE), 0f),
                        ColorStop(Color(0xFF23D3FD), 1f)
                    )

                }
            }

            View {
                attr {
                    allCenter()
                    margin(20f)
                }
                Button {
                    attr {

                        borderRadius(20f)
                        padding(10f)
                        backgroundLinearGradient(
                            Direction.TO_BOTTOM,
                            ColorStop(Color(0xAA23D3FD), 0f),
                            ColorStop(Color(0xAAAD37FE), 1f)
                        )

                        titleAttr {
                            text(EXHABITION_NAME )
                            fontSize(17f)
                            color(Color.WHITE)
                        }
                    }
                    event {
                        click {
                            ctx.jumpToExhibitionCenter()
                        }
                    }
                }
                
                // 测试详情页按钮
                Button {
                    attr {
                        borderRadius(20f)
                        padding(10f)
                        marginTop(15f)
                        backgroundLinearGradient(
                            Direction.TO_BOTTOM,
                            ColorStop(Color(0xAA28A745), 0f),
                            ColorStop(Color(0xAA20C997), 1f)
                        )

                        titleAttr {
                            text("测试详情页")
                            fontSize(17f)
                            color(Color.WHITE)
                        }
                    }
                    event {
                        click {
                            // 创建测试数据
                            val testArtwork = JSONObject().apply {
                                put("Name", "测试艺术作品")
                                put("Material", "油画")
                                put("Description", "这是一个用于测试的示例艺术作品，展示了详情页的功能。")
                                put("Cover", JSONObject().apply {
                                    put("FilePath", "https://via.placeholder.com/300x200?text=Test+Artwork")
                                })
                            }
                            
                            val pageData = JSONObject().apply {
                                put("artwork", testArtwork)
                            }
                            
                            bridgeModule.log("🧪 测试详情页，作品数据: ${testArtwork.toString()}")
                            getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                .openPage("artworkDetail", pageData)
                        }
                    }
                }

            }
        }

    }

    override fun created() {
        super.created()
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        val cacheInputText =
            acquireModule<SharedPreferencesModule>(SharedPreferencesModule.MODULE_NAME).getItem(
                CACHE_KEY
            )
        if (cacheInputText.isNotEmpty()) {
            inputRef.view?.setText(cacheInputText)
        }
    }

    private fun jumpPage(inputText: String) {
        val params = urlParams("pageName=$inputText")
        val pageData = JSONObject()
        params.forEach {
            pageData.put(it.key, it.value)
        }
        val pageName = pageData.optString("pageName")
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).openPage(pageName, pageData)
    }

    private fun jumpToExhibitionCenter() {
        val pageData = JSONObject()
        pageData.put("pageName", "exhibitionCenter")
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).openPage("exhibitionCenter", pageData)
    }

    companion object {
        const val PLACEHOLDER = "输入pageName"
        const val TIP = "输入规则label：router 或者 router&key=value (&后面为页面参数)"
        const val CACHE_KEY = "router_last_input_key2"
        const val BG_URL =
            "https://sqimg.qq.com/qq_product_operations/kan/images/viola/viola_bg.jpg"
        const val LOGO = "https://vfiles.gtimg.cn/wuji_dashboard/xy/componenthub/Dfnp7Q9F.png"
        const val JUMP_TEXT = "跳转"
        const val TEXT_KEY = "text"
        const val TITLE = "Kuikly页面路由-多端测试"
        private const val AAR_MODE_TIP = "如：router 或者 router&key=value （&后面为页面参数）"
        const val EXHABITION_NAME = "会展中心"
    }

}

internal class RouterNavigationBar : ComposeView<RouterNavigationBarAttr, ComposeEvent>() {
    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

    override fun createAttr(): RouterNavigationBarAttr {
        return RouterNavigationBarAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    paddingTop(ctx.pagerData.statusBarHeight)
                    backgroundColor(Color.WHITE)
                }
                // nav bar
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

                vif({ !ctx.attr.backDisable }) {
                    Image {
                        attr {
                            absolutePosition(
                                top = 12f + getPager().pageData.statusBarHeight,
                                left = 12f,
                                bottom = 12f,
                                right = 12f
                            )
                            size(10f, 17f)
                            src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAASBAMAAAB/WzlGAAAAElBMVEUAAAAAAAAAAAAAAAAAAAAAAADgKxmiAAAABXRSTlMAIN/PELVZAGcAAAAkSURBVAjXYwABQTDJqCQAooSCHUAcVROCHBiFECTMhVoEtRYA6UMHzQlOjQIAAAAASUVORK5CYII=")
                        }
                        event {
                            click {
                                getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    .closePage()
                            }
                        }
                    }
                }

            }
        }
    }
}

internal class RouterNavigationBarAttr : ComposeAttr() {
    var title: String by observable("")
    var backDisable = false
}

internal fun ViewContainer<*, *>.RouterNavBar(init: RouterNavigationBar.() -> Unit) {
    addChild(RouterNavigationBar(), init)
}