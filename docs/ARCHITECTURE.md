# 项目架构文档

## 概述

Demo-GoGoGo 是一个基于 MVVM 架构的虚拟位置模拟应用，支持百度地图和高德地图，包含路线创建、管理、模拟和收藏夹等功能。

## 目录结构

```
app/src/main/java/com/zcshou/gogogo/
├── data/
│   ├── database/          # Room 数据库相关
│   ├── entity/            # 数据库实体类
│   ├── dao/               # 数据访问对象接口
│   └── repository/        # 数据仓库层
├── ui/
│   ├── base/              # 基础类
│   ├── viewmodel/         # ViewModel 层
│   └── dialog/            # 对话框
├── adapter/               # RecyclerView 适配器
├── service/               # 后台服务
├── map/                   # 地图引擎
├── utils/                 # 工具类
├── FavoriteActivity.java
├── MainActivity.java
├── RouteActivity.java
├── RouteCreateActivity.java
├── RouteRunActivity.java
└── ...
```

## 架构设计

### MVVM 架构

应用采用 Model-View-ViewModel 架构：

```
View (Activity/Fragment)
    ↑
ViewModel (LiveData 观察)
    ↑
Repository
    ↑
DAO / SharedPreferences
    ↑
Database / Storage
```

### 核心模块

#### 1. 数据层 (Data Layer)

- **Entity**: 定义数据库表结构
- **DAO**: 数据访问对象接口
- **Database**: Room 数据库管理
- **Repository**: 数据仓库，负责数据操作

#### 2. UI 层 (UI Layer)

- **Activity**: 负责界面展示和用户交互
- **ViewModel**: 管理界面数据和业务逻辑
- **Adapter**: RecyclerView 适配器

#### 3. 服务层 (Service Layer)

- **ServiceGo**: 前台服务，负责模拟位置
- **RouteSimulator**: 路线模拟逻辑封装

#### 4. 地图引擎 (Map Engine)

- **MapEngine**: 地图引擎接口
- **MapEngineFactory**: 地图引擎工厂
- **BaiduMapEngine**: 百度地图引擎实现
- **AMapEngine**: 高德地图引擎实现

## 主要类说明

### Repository 层

- **BaseRepository**: 所有 Repository 的基类
- **RepositoryManager**: Repository 单例管理
- **FavoriteRepository**: 收藏数据仓库
- **RouteRepository**: 路线数据仓库
- **LocationRepository**: 位置历史数据仓库
- **SearchRepository**: 搜索历史数据仓库
- **SettingsRepository**: 设置数据仓库

### ViewModel 层

- **BaseViewModel**: 所有 ViewModel 的基类
- **MainViewModel**: 主界面 ViewModel
- **RouteViewModel**: 路线管理 ViewModel
- **RouteCreateViewModel**: 路线创建 ViewModel
- **RouteRunViewModel**: 路线模拟 ViewModel
- **FavoriteViewModel**: 收藏夹 ViewModel

### Service 层

- **ServiceGo**: 定位模拟前台服务
- **RouteSimulator**: 路线模拟逻辑

## 技术栈

- **语言**: Java
- **架构**: MVVM
- **数据库**: Room
- **UI 设计**: Material Design 3
- **地图**: 百度地图 SDK、高德地图 SDK
- **异步**: LiveData, HandlerThread

## 生命周期管理

- **ServiceGo**: 前台服务，使用 startForegroundService 启动
- **ViewModel**: 感知 Activity 生命周期
- **LiveData**: 自动处理生命周期感知
