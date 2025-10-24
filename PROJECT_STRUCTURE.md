# PredictApp 项目结构说明

## 项目概述

PredictApp是一个Android预测应用，现在新增了新闻获取与分析功能。项目采用前后端分离架构：

- `app/` - Android前端应用
- `backend/` - Java后端服务（Spring Boot）

## 项目结构

```
predict app/
├── app/                          # Android应用模块
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/predictapp/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/                 # API接口定义
│   │   │   │   │   ├── model/               # 数据模型
│   │   │   │   │   ├── repository/          # 数据仓库
│   │   │   │   │   ├── service/             # 网络服务
│   │   │   │   │   └── PredictDatabase.kt   # 数据库配置
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/              # UI屏幕组件
│   │   │   │   │   ├── theme/               # 主题配置
│   │   │   │   │   └── viewmodel/           # 视图模型
│   │   │   │   ├── utils/                   # 工具类
│   │   │   │   ├── MainActivity.kt          # 主Activity
│   │   │   │   ├── PredictApp.kt            # 应用主组件
│   │   │   │   └── PredictApplication.kt    # 应用类
│   │   │   ├── res/                         # 资源文件
│   │   │   └── AndroidManifest.xml          # 应用配置
│   │   └── test/                            # 测试代码
│   └── build.gradle                         # 应用构建配置
├── backend/                      # 后端服务模块
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/predictapp/news/
│   │   │   │   ├── config/                  # 配置类
│   │   │   │   ├── controller/              # 控制器层
│   │   │   │   ├── model/                   # 数据模型
│   │   │   │   ├── service/                 # 服务层
│   │   │   │   └── NewsAnalysisApplication.java  # 应用主类
│   │   │   └── resources/
│   │   │       └── application.properties    # 应用配置
│   │   └── test/                            # 测试代码
│   │       └── java/com/predictapp/news/service/ # 服务测试
│   ├── build.gradle                         # 后端构建配置
│   ├── settings.gradle                      # 后端设置
│   ├── start.bat                            # Windows启动脚本
│   └── start.sh                             # Linux/Mac启动脚本
├── README.md                 # 项目说明文档
├── API_KEY_GUIDE.md          # API密钥获取指南
├── PROJECT_STRUCTURE.md      # 项目结构说明
├── build.gradle              # 根项目构建配置
├── gradle.properties         # Gradle配置
└── settings.gradle           # 根项目设置
```

## 核心功能模块

### 1. 新闻分析功能入口
- 文件：`app/src/main/java/com/predictapp/ui/screen/HomeScreen.kt`
- 功能：在首页添加"新闻分析"按钮，点击后跳转到分析页面

### 2. 新闻分析结果展示
- 文件：`app/src/main/java/com/predictapp/ui/screen/NewsAnalysisScreen.kt`
- 功能：展示新闻分析结果，包括摘要、关键要点、情感分析和预测

### 3. 导航系统更新
- 文件：`app/src/main/java/com/predictapp/PredictApp.kt`
- 功能：添加新的导航路由和底部导航栏项

### 4. 网络请求实现
- 文件：
  - `app/src/main/java/com/predictapp/data/api/NewsAnalysisApi.kt`
  - `app/src/main/java/com/predictapp/data/service/NewsAnalysisService.kt`
  - `app/src/main/java/com/predictapp/data/model/NewsAnalysisResult.kt`
- 功能：实现与后端服务的通信

### 5. 后端服务
- 文件：
  - `backend/src/main/java/com/predictapp/news/controller/NewsAnalysisController.java`
  - `backend/src/main/java/com/predictapp/news/service/NewsAnalysisService.java`
  - `backend/src/main/java/com/predictapp/news/service/NewsAPIService.java`
  - `backend/src/main/java/com/predictapp/news/service/LLMService.java`
  - `backend/src/main/java/com/predictapp/news/model/NewsAnalysisResponse.java`
- 功能：处理新闻获取、LLM分析和数据处理

## 数据流说明

1. 用户在Android应用中点击"新闻分析"按钮
2. Android应用通过Retrofit向后端发送HTTP请求
3. 后端服务调用聚合数据新闻API获取最新财经新闻
4. 后端服务构建提示词并调用ModelScope LLM API进行分析
5. LLM返回分析结果，后端服务处理并格式化数据
6. 后端服务将结果返回给Android应用
7. Android应用展示分析结果给用户

## 配置文件

### Android应用配置
- `app/src/main/AndroidManifest.xml` - 应用权限和配置
- `app/build.gradle` - 应用依赖和构建配置

### 后端服务配置
- `backend/src/main/resources/application.properties` - 服务配置和API密钥
- `backend/build.gradle` - 后端依赖和构建配置

## 测试文件

- `app/src/test/` - Android应用单元测试
- `backend/src/test/java/com/predictapp/news/service/NewsAnalysisServiceTest.java` - 后端服务单元测试

## 启动脚本

- `backend/start.bat` - Windows环境下启动后端服务
- `backend/start.sh` - Linux/Mac环境下启动后端服务

## 资源文件

- `app/src/main/res/` - Android应用资源文件（图标、字符串、主题等）