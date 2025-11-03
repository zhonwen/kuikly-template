package com.example.kuikly_test_project

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.reactive.handler.*
import com.example.kuikly_test_project.base.BasePager
import com.example.kuikly_test_project.base.bridgeModule
import com.example.kuikly_test_project.base.PlatformUtils
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.views.layout.*
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.directives.vfor

/**
 * 会展中心页面
 * 
 * 功能特性：
 * 1. 支持分页加载艺术作品列表
 * 2. 实现滑到底部自动翻页
 * 3. 支持下拉刷新
 * 4. 优化了接口数据结构解析 (ReturnData.Items, ReturnData.PageSize, ReturnData.PageIndex, ReturnData.RecordCount)
 * 5. 完善的错误处理和状态管理
 * 6. 响应式UI更新
 */
@Page("exhibitionCenter", supportInLocal = true)
internal class ExhibitionCenterPage : BasePager() {
    
    // 使用响应式列表存储数据
    private var artworkList by observableList<JSONObject>()
    private var isLoading by observable(false)
    
    // 分页相关状态
    private var currentPage by observable(0)
    private var pageSize by observable(10)
    private var totalCount by observable(0)
    private var hasMoreData by observable(true)
    private var isLoadMore by observable(false)
    private var isRefreshing by observable(false)
    
    // 防重复触发机制
    private var lastScrollEndTime by observable(0L)
    
    // 使用ScrollParams参数检测是否到达底部
    private fun checkIfReachedBottomWithScrollParams(scrollParams: ScrollParams) {
        try {
            // 获取ScrollParams中的所有参数
            val offsetX = scrollParams.offsetX
            val offsetY = scrollParams.offsetY
            val contentWidth = scrollParams.contentWidth
            val contentHeight = scrollParams.contentHeight
            val viewWidth = scrollParams.viewWidth
            val viewHeight = scrollParams.viewHeight
            val isDragging = scrollParams.isDragging
            
            // 记录滚动历史，用于判断滚动方向
            // val currentTime = System.currentTimeMillis()
            // val timeDiff = currentTime - lastScrollEndTime
            
            // bridgeModule.log("=== 滚动结束触底检测开始 ===")
            // bridgeModule.log("滚动参数:")
            // bridgeModule.log("  - 当前偏移: X=$offsetX, Y=$offsetY")
            // bridgeModule.log("  - 内容尺寸: ${contentWidth}×${contentHeight}")
            // bridgeModule.log("  - 视图尺寸: ${viewWidth}×${viewHeight}")
            // bridgeModule.log("  - 是否拖拽中: $isDragging")
            // bridgeModule.log("  - 时间间隔: ${timeDiff}ms")
            
            // 判断是否到达底部的核心逻辑
            // 垂直方向：当前偏移量 + 视图高度 >= 内容总高度
            var isAtBottom = false
            isAtBottom = (offsetY + viewHeight) >= (contentHeight - 0)
            
            // 添加容差处理，解决触底检测不准确的问题
            val tolerance = 100f // 100像素容差
            val isAtBottomWithTolerance = (offsetY + viewHeight) >= (contentHeight - tolerance)
            
            // 更智能的触底检测：当allHeight接近contentHeight时认为是触底
            val allHeight = offsetY + viewHeight
            val heightDifference = contentHeight - allHeight
            val isNearBottom = heightDifference <= tolerance
            val isVeryNearBottom = heightDifference <= (tolerance * 2) // 200像素容差
            
            // 结合拖拽状态的智能判断
            val shouldTriggerLoad = when {
                // 如果正在拖拽，使用更宽松的触底检测
                isDragging -> isAtBottom || isAtBottomWithTolerance || isNearBottom
                // 如果滚动结束，使用标准触底检测
                else -> isAtBottom || isAtBottomWithTolerance
            }
            
            
            // 水平方向：当前偏移量 + 视图宽度 >= 内容总宽度（如果需要的话）
            val isAtRightEdge = (offsetX + viewWidth) >= (contentWidth - 20)
            
            // 使用多种触底检测方式，更可靠
            if (shouldTriggerLoad) {
                // 只有在底部时才检查是否需要加载更多
                checkAndLoadMore()
            } else if (isVeryNearBottom && !isDragging) {
                checkAndLoadMore()
            } else {
            }
            
        } catch (e: Exception) {
            checkAndLoadMore()
        }
    }

    override fun created() {
        super.created()
        // 页面创建时自动获取数据
        fetchArtworkList(true)
    }

    override fun body(): ViewBuilder {
        val ctx = this
        
        return {
            attr {
                backgroundColor(Color.WHITE)
            }
            
            // 导航栏
            ExhibitionNavBar {
                attr {
                    title = TITLE
                    // backDisable = true
                }
            }

            // 主要内容区域 - 垂直列表展示
            View {
                attr {
                    flex(1f)
                    marginTop(PlatformUtils.getStatusBarHeight() + 10f) // 动态获取状态栏高度+10f，适配不同平台
                    paddingLeft(20f)
                    paddingRight(20f)
                    paddingTop(20f)
                    paddingBottom(20f)
                }
                
                // 数据状态显示和刷新按钮
                View {
                    attr {
                        width(pagerData.pageViewWidth - 40f)
                        height(60f)
                        backgroundColor(Color(0xFFF0F0F0))
                        borderRadius(8f)
                        padding(15f)
                        marginBottom(20f)
                        flexDirectionRow()
                        justifyContentSpaceBetween()
                        alignItemsCenter()
                    }
                    
                    Text {
                        attr {
                            text("第${ctx.currentPage + 1}页 | 共${ctx.totalCount}件作品 | 已加载${ctx.artworkList.size}件${if (ctx.hasMoreData) " | 还有更多" else " | 已加载完"}")
                            fontSize(14f)
                            color(Color(0xFF333333))
                            flex(1f)
                        }
                    }
                    
                    // 刷新按钮
                    View {
                        attr {
                            width(80f)
                            height(30f)
                            backgroundColor(Color(0xFF007AFF))
                            borderRadius(15f)
                            allCenter()
                        }
                        
                        Text {
                            attr {
                                text("刷新")
                                fontSize(12f)
                                color(Color.WHITE)
                            }
                        }
                        
                        event {
                            click {
                                if (!ctx.isRefreshing && !ctx.isLoading) {
                                    ctx.fetchArtworkList(true)
                                }
                            }
                        }
                    }
                }
                
                // 使用Scroller包装内容，支持滚动
                Scroller {
                    attr {
                        width(pagerData.pageViewWidth - 40f)
                        height(pagerData.pageViewHeight - 180f) // 减去导航栏和状态栏的高度
                        showScrollerIndicator(true) // 显示滚动指示器
                        flexDirectionColumn() // 垂直方向滚动
                    }
                    
                    event {
                        scroll {
                            bridgeModule.log("页面滚动: x=${it.offsetX.toInt()}, y=${it.offsetY.toInt()}")
                            
                            // 检测下拉刷新手势
                            if (it.offsetY < -50 && !ctx.isRefreshing && !ctx.isLoading) {
                                ctx.handlePullToRefresh()
                            }
                        }
                        scrollEnd { scrollParams ->
                            // bridgeModule.log("页面滚动结束")
                            // bridgeModule.log("ScrollParams详情:")
                            // bridgeModule.log("  - offsetX: ${scrollParams.offsetX}")
                            // bridgeModule.log("  - offsetY: ${scrollParams.offsetY}")
                            // bridgeModule.log("  - contentWidth: ${scrollParams.contentWidth}")
                            // bridgeModule.log("  - contentHeight: ${scrollParams.contentHeight}")
                            // bridgeModule.log("  - viewWidth: ${scrollParams.viewWidth}")
                            // bridgeModule.log("  - viewHeight: ${scrollParams.viewHeight}")
                            // bridgeModule.log("  - isDragging: ${scrollParams.isDragging}")
                            
                            // 添加时间节流，防止重复触发（800ms内只允许触发一次）
                            // val currentTime = System.currentTimeMillis()
                            // if (currentTime - ctx.lastScrollEndTime > 800) {
                                // ctx.lastScrollEndTime = currentTime
                                // 使用ScrollParams检测是否到达底部，只有在底部时才加载更多
                                ctx.checkIfReachedBottomWithScrollParams(scrollParams)
                            // } else {
                            //     bridgeModule.log("滚动结束事件被节流，跳过处理")
                            // }
                        }
                    }
                    
                    // 下拉刷新区域
                    // View {
                    //     attr {
                    //         width(pagerData.pageViewWidth - 40f)
                    //         height(50f)
                    //         marginBottom(20f)
                    //         allCenter()
                    //     }
                        
                    //     if (ctx.isRefreshing) {
                    //         Text {
                    //             attr {
                    //                 text("正在刷新...")
                    //                 fontSize(14f)
                    //                 color(Color(0xFF999999))
                    //                 textAlignCenter()
                    //             }
                    //         }
                    //     } else {
                    //         Text {
                    //             attr {
                    //                 text("下拉刷新")
                    //                 fontSize(14f)
                    //                 color(Color(0xFF999999))
                    //                 textAlignCenter()
                    //             }
                    //         }
                    //     }
                    // }
                
                // 作品列表展示区域 - 使用vfor进行响应式渲染
                View {
                    attr {
                        width(pagerData.pageViewWidth - 40f)
                        backgroundColor(Color.GRAY)
                        borderRadius(8f)
                        padding(20f)
                        marginBottom(20f)
                    }
                    
                    // 列表标题
                    Text {
                        attr {
                            text("📋 作品列表 - 共${ctx.totalCount}件作品")
                            fontSize(18f)
                            color(Color.WHITE)
                            textAlignCenter()
                            marginBottom(20f)
                        }
                    }
                    
                    // 使用vfor进行响应式列表渲染
                    vfor({ ctx.artworkList }) { artwork ->
                        val index = ctx.artworkList.indexOf(artwork) + 1
                        // 作品卡片
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 80f)
                                minHeight(200f) // 增加高度以容纳图片
                                backgroundColor(Color.WHITE)
                                borderRadius(8f)
                                marginBottom(15f)
                                padding(15f)
                                // 添加点击效果
                                // cursorPointer() // 暂时注释掉，可能不支持此方法
                            }
                            
                            event {
                                click {
                                    // 跳转到作品详情页
                                    bridgeModule.log("🎨 作品被点击: ${artwork.optString("Name", "未知作品")}")
                                    val pageData = JSONObject().apply {
                                        put("artwork", artwork)
                                    }
                                    getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        .openPage("artworkDetail", pageData)
                                }
                            }
                            
                            // 作品图片 - 优先显示Cover.FilePath，如果没有则显示Thumbnail
                            var imageUrl = ""
                            val cover = artwork.optJSONObject("Cover")
                            if (cover != null) {
                                imageUrl = cover.optString("FilePath", "")
                            }
                            if (imageUrl.isEmpty()) {
                                imageUrl = artwork.optString("Thumbnail", "")
                            }
                            
                            if (imageUrl.isNotEmpty()) {
                                Image {
                                    attr {
                                        width(pagerData.pageViewWidth - 110f)
                                        height(120f)
                                        borderRadius(8f)
                                        src(imageUrl)
                                        backgroundColor(Color(0xFFF5F5F5))
                                        marginBottom(10f)
                                    }
                                }
                            } else {
                                // 如果没有图片，显示占位符
                                View {
                                    attr {
                                        width(pagerData.pageViewWidth - 110f)
                                        height(120f)
                                        borderRadius(8f)
                                        backgroundColor(Color(0xFFF5F5F5))
                                        marginBottom(10f)
                                        allCenter()
                                    }
                                    
                                    Text {
                                        attr {
                                            text("暂无图片")
                                            fontSize(12f)
                                            color(Color(0xFF999999))
                                        }
                                    }
                                }
                            }
                            
                            // 作品标题 - 优先显示Name，如果没有则显示Title
                            var title = artwork.optString("Name", "")
                            if (title.isEmpty()) {
                                title = artwork.optString("Title", "")
                            }
                            if (title.isEmpty()) {
                                title = "未命名作品"
                            }
                            
                            Text {
                                attr {
                                    text("${index}. $title")
                                    fontSize(16f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginBottom(8f)
                                }
                            }
                            
                            // 作品材质 - 优先显示Material，如果没有则显示其他相关字段
                            var material = artwork.optString("Material", "")
                            if (material.isEmpty()) {
                                material = artwork.optString("ArtworkType", "")
                            }
                            if (material.isNotEmpty()) {
                                Text {
                                    attr {
                                        text("材质: $material")
                                        fontSize(12f)
                                        color(Color(0xFF666666))
                                        marginBottom(5f)
                                    }
                                }
                            }
                            
                            // 作品尺寸 - 显示SizeHtml或Size
                            val size = artwork.optString("SizeHtml", "")
                            if (size.isEmpty()) {
                                val sizeAlt = artwork.optString("Size", "")
                                if (sizeAlt.isNotEmpty()) {
                                    Text {
                                        attr {
                                            text("尺寸: $sizeAlt")
                                            fontSize(11f)
                                            color(Color(0xFF888888))
                                        }
                                    }
                                }
                            } else {
                                Text {
                                    attr {
                                        text("尺寸: $size")
                                        fontSize(11f)
                                        color(Color(0xFF888888))
                                    }
                                }
                            }
                        }
                    }
                
                }
            } // Scroller结束
            }
        }
    }

    // 处理下拉刷新
    private fun handlePullToRefresh() {
        if (!isRefreshing && !isLoading) {
            bridgeModule.log("检测到下拉刷新手势")
            fetchArtworkList(true)
        }
    }
    
    // 检查是否需要加载更多数据
    private fun checkAndLoadMore() {
        bridgeModule.log("=== 滚动结束触底加载检查开始 ===")
        // 修复：简化翻页条件判断，避免复杂的计算
        // 判断条件：
        // 1. 还有更多数据 (hasMoreData)
        // 2. 当前不在加载中 (!isLoading)
        // 3. 当前不在加载更多状态 (!isLoadMore)
        // 4. 当前不在刷新状态 (!isRefreshing)
        // 5. 当前已加载数据量小于总数（双重保险）
        if (hasMoreData && !isLoading && !isLoadMore && !isRefreshing && artworkList.size < totalCount) {
            // bridgeModule.log("✅ 满足滚动结束触底加载条件，准备加载更多数据")
            // bridgeModule.log("当前状态:")
            // bridgeModule.log("  - 页码: $currentPage")
            // bridgeModule.log("  - 总数量: $totalCount")
            // bridgeModule.log("  - 已加载: ${artworkList.size}")
            // bridgeModule.log("  - 每页大小: $pageSize")
            // bridgeModule.log("  - 还有更多数据: $hasMoreData")
            
            // bridgeModule.log("即将请求下一页，PageIndex参数将是: $currentPage")
            currentPage++
            // 调用接口加载下一页数据
            fetchArtworkList(false)
        } else {
            val reason = when {
                !hasMoreData -> "没有更多数据"
                isLoading -> "正在加载中"
                isLoadMore -> "正在加载更多"
                isRefreshing -> "正在刷新"
                artworkList.size >= totalCount -> "已加载完所有数据"
                else -> "未知原因"
            }
        }
    }

    // 获取艺术作品列表
    private fun fetchArtworkList(isRefresh: Boolean = false) {
        if (isLoading) return
        
        if (isRefresh) {
            // 刷新时重置分页状态
            currentPage = 0
            artworkList.clear()
            hasMoreData = true
            isRefreshing = true
        } else {
            // 加载更多时检查是否还有数据
            if (!hasMoreData) return
            isLoadMore = true
        }
        
        isLoading = true
        // bridgeModule.log("=== 开始请求艺术作品列表 ===")
        // bridgeModule.log("请求模式: ${if (isRefresh) "刷新" else "加载更多"}")
        // bridgeModule.log("当前页码: $currentPage, 每页大小: $pageSize")
        // bridgeModule.log("POST请求URL: http://dome.dome.me/WebApi/Artwork/GetList")

        
        // 创建请求参数 - 按照接口格式组织
        val requestParams = JSONObject().apply {
            put("ArtworkType", "")
            put("CreateTime", "")
            put("CreateYear", "")
            put("DeviceType", 100)
            put("ImageShowConfig", 2)
            put("PageIndex", currentPage)
            put("PageSize", pageSize)
            put("Status", "")
        }
        bridgeModule.log("请求参数: ${requestParams.toString()}")
        // 输出请求参数日志
        // bridgeModule.log("POST请求URL: http://dome.dome.me/WebApi/Artwork/GetList")
        // bridgeModule.log("当前页码: $currentPage, 每页大小: $pageSize")

        // 使用kuikly官方的acquireModule<NetworkModule>方法发起POST请求
        acquireModule<NetworkModule>(NetworkModule.MODULE_NAME).httpRequest(
            "http://dome.dome.me/WebApi/Artwork/GetList",
            true, // isPost = true 表示POST请求
            param = requestParams,
            responseCallback = { data, success, errorMsg ->
                isLoading = false
                isRefreshing = false
                isLoadMore = false
                
                
                if (success && data != null) {
                    try {
                        bridgeModule.log("请求成功，解析结果: ${data.toString()}")
                        
                        // 解析返回的数据结构 - 使用新的数据结构
                        val code = data.optInt("Code")
                        val description = data.optString("Description", "")
                        val returnData = data.optJSONObject("ReturnData")
                        
                        // bridgeModule.log("响应Code: $code")
                        // bridgeModule.log("响应Description: $description")
                        
                        if (code == 0 && returnData != null) { // 接口成功码是0
                            // 解析分页信息
                            val pageSize = returnData.optInt("PageSize", 24)
                            val pageIndex = returnData.optInt("PageIndex", 0)
                            val recordCount = returnData.optInt("RecordCount", 0)
                            val pageCount = returnData.optInt("PageCount", 0)
                            
                            // bridgeModule.log("分页信息: PageSize=$pageSize, PageIndex=$pageIndex, RecordCount=$recordCount, PageCount=$pageCount")
                            // bridgeModule.log("页码对比: 请求时currentPage=$currentPage, 接口返回pageIndex=$pageIndex")
                            
                            // 更新分页状态
                            this.pageSize = pageSize
                            this.totalCount = recordCount
                            
                            // 获取作品数据
                            val items = returnData.optJSONArray("Items") ?: JSONArray()
                            
                            // 解析作品数据到列表
                            val artworkData = mutableListOf<JSONObject>()
                            
                            for (i in 0 until items.length()) {
                                val item = items.optJSONObject(i)
                                if (item != null) {
                                    val cover = item.optJSONObject("Cover")
                                    if (cover != null) {
                                        val filePath = cover.optString("FilePath", "")
                                        bridgeModule.log("作品图片路径: $filePath")
                                    } else {
                                        bridgeModule.log("未找到Cover字段")
                                    }
                                    
                                    // 检查Material字段
                                    val material = item.optString("Material", "")
                                
                                    artworkData.add(item)
                                }
                            }
                            
                            if (isRefresh) {
                                // 刷新时清空现有数据并添加新数据
                                artworkList.clear()
                                artworkList.addAll(artworkData)
                            } else {
                                // 加载更多时追加数据
                                artworkList.addAll(artworkData)
                            }
                            
                            // 更新分页状态
                            if (isRefresh) {
                                currentPage = 0
                            } else {
                                // 修复：加载更多时，应该递增页码
                                // 因为接口返回的pageIndex可能不准确，我们需要主动管理页码
                                // currentPage++
                                // bridgeModule.log("页码递增: 从 ${pageIndex} 增加到 $currentPage")
                            }
                            // 修复：正确判断是否还有更多数据
                            // 需要同时满足两个条件：
                            // 1. 当前页码小于总页数-1（页码检查）
                            // 2. 当前已加载的数据量小于总数（数据量检查）
                            val hasMorePages = pageIndex < pageCount - 1
                            val hasMoreDataByCount = artworkList.size < totalCount
                            hasMoreData = hasMorePages && hasMoreDataByCount && artworkData.isNotEmpty()
                            
                            // bridgeModule.log("分页状态更新: currentPage=$currentPage, pageIndex=$pageIndex, pageCount=$pageCount")
                            // bridgeModule.log("数据量检查: 已加载=${artworkList.size}, 总数=$totalCount, 当前页数据=${artworkData.size}")
                            // bridgeModule.log("更多数据判断: 有更多页=$hasMorePages, 有更多数据=$hasMoreDataByCount, 最终结果=$hasMoreData")
                            
                            // bridgeModule.log("接口数据解析完成，artworkList大小: ${artworkList.size}")
                            // bridgeModule.log("分页状态: currentPage=$currentPage, hasMoreData=$hasMoreData")
                            
                            if (isRefresh) {
                                // bridgeModule.toast("数据刷新完成，共${artworkList.size}件作品")
                            } else {
                                // bridgeModule.toast("加载更多完成，共${artworkList.size}件作品")
                            }
                            
                        } else {
                            // bridgeModule.log("请求失败，Code: $code, Description: $description")
                            // bridgeModule.toast("加载失败: $description")
                            
                            // 如果接口失败，使用模拟数据作为备选
                            if (isRefresh) {
                                createMockDataAsFallback()
                            } else {
                                // 加载更多失败时，回退页码
                                currentPage = maxOf(0, currentPage - 1)
                                // bridgeModule.log("加载更多失败，页码回退到: $currentPage")
                            }
                        }
                        
                    } catch (e: Exception) {
                        // bridgeModule.log("解析响应结果失败: ${e.message}")
                        // bridgeModule.toast("数据解析失败，使用模拟数据")
                        
                        // 解析失败时使用模拟数据
                        if (isRefresh) {
                            createMockDataAsFallback()
                        }
                    }
                } else {
                    // bridgeModule.log("请求失败: $errorMsg")
                    // bridgeModule.toast("网络请求失败，使用模拟数据")
                    
                    // 网络请求失败时使用模拟数据
                    if (isRefresh) {
                        createMockDataAsFallback()
                    } else {
                        // 加载更多失败时，回退页码
                        currentPage = maxOf(0, currentPage - 1)
                        // bridgeModule.log("网络请求失败，页码回退到: $currentPage")
                    }
                }
            }
        )
    }
    
    // 创建模拟数据作为备选方案
    private fun createMockDataAsFallback() {
        bridgeModule.log("创建模拟数据作为备选方案...")
        
        val mockData = mutableListOf<JSONObject>()
        
        for (i in 1..10) {
            val artwork = JSONObject().apply {
                put("Name", "备选模拟作品 $i")
                put("Material", "油画颜料")
                put("Cover", JSONObject().apply {
                    put("FilePath", "https://via.placeholder.com/300x200?text=Fallback$i")
                })
            }
            mockData.add(artwork)
        }
        
        artworkList.clear()
        artworkList.addAll(mockData)
        totalCount = mockData.size
        currentPage = 0
        hasMoreData = false
        bridgeModule.log("模拟数据创建完成，大小: ${artworkList.size}")
    }
    
    companion object {
        const val TITLE = "会展中心"
    }
}

// 会展中心导航栏组件
internal class ExhibitionNavigationBar : ComposeView<ExhibitionNavigationBarAttr, ComposeEvent>() {
    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

    override fun createAttr(): ExhibitionNavigationBarAttr {
        return ExhibitionNavigationBarAttr()
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
                            bridgeModule.log("🔙 返回按钮被点击 - 开始关闭页面")
                            getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                .closePage()
                        }
                    }
                }
            }
        }
    }
}

internal class ExhibitionNavigationBarAttr : ComposeAttr() {
    var title: String by observable("")
}

internal fun ViewContainer<*, *>.ExhibitionNavBar(init: ExhibitionNavigationBar.() -> Unit) {
    addChild(ExhibitionNavigationBar(), init)
}