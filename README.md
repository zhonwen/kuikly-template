# 艺术展览系统

这是一个基于Kuikly框架开发的多端艺术展览展示系统，支持Android、iOS和HarmonyOS平台。

## 功能特性

### 🎨 艺术展览列表页面 (ArtExhibitionListPage)
- **智能搜索**: 支持按展览名称、艺术家、类别、地点进行搜索
- **实时搜索**: 输入时自动触发搜索，支持防抖处理
- **分页加载**: 支持上拉滚动翻页，自动加载更多数据
- **美观界面**: 卡片式布局，展示展览图片、标题、艺术家、评分等信息
- **响应式设计**: 适配不同屏幕尺寸

### 📖 艺术展览详情页面 (ArtExhibitionDetailPage)
- **详细信息**: 展示展览的完整信息，包括描述、时间、地点、价格等
- **图片展示**: 大图展示展览主图
- **交互功能**: 支持预约、收藏、分享等操作
- **导航栏**: 包含返回按钮和分享功能

### 🔍 搜索功能
- **多维度搜索**: 支持按展览名称、艺术家、类别、地点搜索
- **实时过滤**: 搜索结果实时更新
- **智能匹配**: 支持模糊匹配和大小写不敏感搜索

## 页面结构

```
RouterPage (路由页面)
├── 输入框：输入任意内容
├── 跳转按钮：点击后跳转到艺术展览列表
└── 传递搜索关键词到列表页面

ArtExhibitionListPage (展览列表页面)
├── 搜索栏：实时搜索功能
├── 展览列表：卡片式展示
├── 分页加载：上拉加载更多
└── 点击跳转：跳转到详情页面

ArtExhibitionDetailPage (展览详情页面)
├── 展览图片：大图展示
├── 基本信息：标题、艺术家、评分等
├── 详细描述：展览介绍
├── 展览详情：时间、地点、价格、类别
└── 操作按钮：预约、收藏、分享
```

## 数据模型

### ArtExhibition (艺术展览)
```kotlin
data class ArtExhibition(
    val id: String,           // 展览ID
    val title: String,        // 展览标题
    val artist: String,       // 艺术家
    val description: String,  // 展览描述
    val imageUrl: String,     // 展览图片
    val startDate: String,    // 开始日期
    val endDate: String,      // 结束日期
    val location: String,     // 展览地点
    val price: String,        // 门票价格
    val category: String,     // 展览类别
    val rating: Float,        // 评分
    val viewCount: Int        // 浏览量
)
```

## 使用方法

1. **启动应用**: 打开应用后进入RouterPage页面
2. **输入搜索**: 在输入框中输入任意内容（如"现代艺术"、"毕加索"等）
3. **点击跳转**: 点击"跳转"按钮，系统会跳转到艺术展览列表页面
4. **浏览展览**: 在列表页面可以浏览所有展览，支持搜索和分页
5. **查看详情**: 点击任意展览卡片，跳转到详情页面查看完整信息
6. **交互操作**: 在详情页面可以进行预约、收藏、分享等操作

## 技术特点

- **多端支持**: 基于Kuikly框架，支持Android、iOS、HarmonyOS
- **响应式UI**: 使用Flexbox布局，适配不同屏幕
- **组件化设计**: 可复用的UI组件
- **数据驱动**: 基于数据模型的界面更新
- **性能优化**: 分页加载、防抖搜索等优化措施

## 文件结构

```
shared/src/commonMain/kotlin/com/example/kuikly_test_project/
├── RouterPage.kt                    # 路由页面
├── ArtExhibitionListPage.kt         # 展览列表页面
├── ArtExhibitionDetailPage.kt       # 展览详情页面
├── model/
│   └── ArtExhibition.kt            # 数据模型
└── base/
    ├── BasePager.kt                 # 基础页面类
    └── BridgeModule.kt              # 桥接模块
```

## 开发说明

- 使用Kotlin Multiplatform开发
- 基于Kuikly框架的声明式UI
- 支持热重载和本地开发
- 图片资源使用本地banner.png文件
- 模拟数据包含5个不同的艺术展览

## 扩展功能

系统预留了以下扩展接口：
- 网络请求集成
- 本地数据缓存
- 用户认证系统
- 支付功能集成
- 社交分享功能
- 推送通知

这个系统提供了一个完整的艺术展览展示解决方案，具有良好的用户体验和扩展性。
