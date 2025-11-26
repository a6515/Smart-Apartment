# 智慧公寓管理系统 - 前端项目

## 项目简介
基于 Vue 3 + Vite + Element Plus 的智慧公寓管理系统前端项目

## 技术栈
- Vue 3.3
- Vite 4.4
- Vue Router 4.2
- Pinia 2.1
- Element Plus 2.3
- Axios 1.5
- ECharts 5.4

## 项目结构
```
frontend/
├── src/
│   ├── api/              # API接口
│   │   ├── auth.js       # 认证接口
│   │   ├── building.js   # 楼宇管理
│   │   ├── room.js       # 房间管理
│   │   ├── user.js       # 用户管理
│   │   ├── checkin.js    # 入住管理
│   │   ├── repair.js     # 报修管理
│   │   └── statistics.js # 统计数据
│   ├── layouts/          # 布局组件
│   │   ├── AdminLayout.vue    # 管理端布局
│   │   └── StudentLayout.vue  # 学生端布局
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia状态管理
│   ├── utils/            # 工具类
│   │   └── request.js    # Axios封装
│   ├── views/            # 页面组件
│   │   ├── admin/        # 管理端页面
│   │   │   ├── Dashboard.vue  # 首页概览
│   │   │   ├── Building.vue   # 楼宇管理
│   │   │   ├── Room.vue       # 房间管理
│   │   │   ├── CheckIn.vue    # 入住管理
│   │   │   ├── Repair.vue     # 报修管理
│   │   │   └── User.vue       # 用户管理
│   │   ├── student/      # 学生端页面
│   │   │   ├── Home.vue       # 学生首页
│   │   │   ├── Room.vue       # 我的房间
│   │   │   ├── CheckIn.vue    # 入住申请
│   │   │   ├── Repair.vue     # 报修服务
│   │   │   └── Profile.vue    # 个人中心
│   │   └── Login.vue     # 登录页
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html
├── vite.config.js
└── package.json
```

## 快速开始

### 1. 安装依赖
```bash
cd frontend
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

访问：http://localhost:3000

### 3. 构建生产版本
```bash
npm run build
```

## 功能模块

### 管理端 (/admin)
- **首页概览** - 统计数据展示、图表分析
- **楼宇管理** - 楼宇的增删改查
- **房间管理** - 房间信息管理
- **入住管理** - 入住申请审批
- **报修管理** - 报修工单处理
- **用户管理** - 用户信息管理（仅管理员）

### 学生端 (/student)
- **学生首页** - 个人信息概览
- **我的房间** - 查看房间信息和室友
- **入住申请** - 提交入住申请
- **报修服务** - 提交报修申请
- **个人中心** - 个人信息维护

## 权限说明

### 用户类型
- **1 - 管理员**：所有功能权限
- **2 - 宿管**：除用户管理外的所有功能
- **3 - 学生**：仅学生端功能

### 路由权限
- 登录后根据用户类型自动跳转对应端
- 前端路由守卫验证权限
- 后端API接口权限验证

## API配置

### 开发环境
后端API地址：http://localhost:8080
通过Vite proxy代理：/api -> http://localhost:8080/api

### 生产环境
修改 `vite.config.js` 中的 proxy 配置

## 默认账号

### 管理员
- 用户名：admin
- 密码：admin123

### 学生
- 注册后使用（默认为学生用户）

## 注意事项

1. 确保后端服务已启动（端口8080）
2. 确保MySQL和Redis服务正常运行
3. 首次使用需要初始化数据库
4. 开发时使用Chrome浏览器以获得最佳体验

## 常见问题

### 1. 页面空白
- 检查后端服务是否启动
- 检查浏览器控制台错误信息
- 检查API请求是否成功

### 2. 登录失败
- 检查用户名密码是否正确
- 检查后端数据库连接
- 检查网络请求状态

### 3. 权限不足
- 确认用户类型是否正确
- 检查路由配置的roles参数
- 检查后端权限注解

## 开发建议

1. 使用Vue DevTools进行调试
2. 遵循Vue 3 Composition API规范
3. 使用Element Plus组件库
4. 保持代码风格一致
5. 及时提交代码到Git

## 更新日志

### v1.0.0 (2025-11-25)
- ✅ 完成项目基础架构
- ✅ 实现管理端所有页面
- ✅ 实现学生端所有页面
- ✅ 完成权限管理系统
- ✅ 完成路由配置和守卫
- ✅ 完成API接口封装

## 许可证
MIT License
