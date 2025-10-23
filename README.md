# 基金行业预测助手

这是一个安卓应用，用于记录用户每日对所持有基金的行业涨跌预测，并自动统计历史所有预测的准确率。

## 功能特性

1. **行业管理** - 添加、编辑、删除行业类别
2. **每日预测** - 针对相关行业进行涨跌方向预测（涨/跌/平）
3. **准确率统计** - 自动跟踪并计算用户历史预测的胜率
4. **数据可视化** - 提供可视化的统计数据展示界面

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **数据库**: Room
- **架构**: MVVM
- **异步处理**: Kotlin Coroutines & Flow

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/predictapp/
│   │   │   ├── data/
│   │   │   │   ├── model/       # 数据模型
│   │   │   │   ├── dao/         # 数据访问对象
│   │   │   │   ├── repository/  # 数据仓库
│   │   │   │   └── PredictDatabase.kt  # 数据库
│   │   │   ├── ui/
│   │   │   │   ├── screen/      # UI屏幕
│   │   │   │   ├── theme/       # 主题样式
│   │   │   │   └── viewmodel/   # 视图模型
│   │   │   ├── utils/           # 工具类
│   │   │   ├── MainActivity.kt  # 主Activity
│   │   │   ├── PredictApp.kt    # 主应用组件
│   │   │   └── PredictApplication.kt  # 应用类
│   │   ├── res/                 # 资源文件
│   │   └── AndroidManifest.xml  # 应用配置
├── build.gradle                 # 应用构建配置
├── proguard-rules.pro           # 混淆规则
└── src/main/assets/             # 静态资源
```

## 数据库设计

### Industry 表
- id: Long (主键)
- name: String (行业名称)

### Prediction 表
- id: Long (主键)
- industryId: Long (行业ID)
- date: Date (预测日期)
- predictedDirection: Direction (预测方向: UP/DOWN/FLAT)
- actualDirection: Direction? (实际方向，可为空)

## 如何运行

1. 使用Android Studio打开项目
2. 等待Gradle同步完成
3. 构建并运行应用

## 屏幕说明

1. **首页 (HomeScreen)** - 行业管理界面
2. **预测 (PredictionScreen)** - 每日预测界面
3. **统计 (StatisticsScreen)** - 准确率统计界面

## 开发计划

- [x] 项目结构搭建
- [x] 数据库设计与实现
- [x] UI界面设计
- [ ] 功能测试与优化
- [ ] 发布准备