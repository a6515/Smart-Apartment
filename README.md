# 智慧公寓管理系统

## 项目简介

本项目是基于 **SpringBoot + Vue + Redis + WebSocket** 前后端分离架构的智慧公寓管理系统，旨在解决传统公寓管理中信息滞后、人工统计繁琐及服务响应慢等问题。系统分为"公寓管理后台"与"学生/住户服务端"两大子系统，支持实时消息推送功能。

**最后更新：** 2025-11-29  
**项目完成度：** 95%  
**核心功能：** 5大业务流程全部实现（入住、换宿、退宿、报修、公告）

## 技术栈

### 后端技术
- **Spring Boot 2.7.18** - 核心框架
- **MyBatis 2.3.1** - 持久层框架
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存中间件
- **Spring WebSocket** - 实时通信 ⭐
- **RabbitMQ** - 消息队列 ⭐
- **JWT 0.11.5** - 身份认证
- **Knife4j 4.1.0** - API接口文档
- **Hutool 5.8.18** - Java工具类库
- **Spring Security** - 密码加密

### 前端技术
- **Vue 3.3.4** - 渐进式JavaScript框架
- **Vite 4.4.9** - 新一代前端构建工具
- **Element Plus 2.3.14** - UI组件库
- **ECharts 5.4.3** - 数据可视化图表
- **Pinia 2.1.6** - 状态管理
- **Axios 1.5.0** - HTTP请求
- **SockJS + STOMP** - WebSocket客户端 ⭐

## 功能模块

### 1. 系统基础管理模块
- ✅ 用户管理（管理员、宿管、学生）
- ✅ 角色权限分配（RBAC模型）
- 菜单管理
- ✅ 系统日志记录

### 2. 公寓资源管理模块
- ✅ 楼宇信息管理（增删改查）
- ✅ 房间管理（楼层、房间号、床位数）
- ✅ 床位管理（实时状态展示）
- 批量导入导出功能

### 3. 入住与退宿管理模块
- ✅ 学生入住申请
- ✅ 宿舍分配审批
- ✅ 退宿办理流程
- ✅ 换宿申请功能 ⭐

### 4. 智慧服务与报修模块
- ✅ 在线报修功能（支持图片上传）
- ✅ 维修人员接单系统
- ✅ 维修进度反馈
- ✅ 满意度评价
- ✅ 公告发布功能 ⭐
- ✅ WebSocket实时推送 ⭐
- ✅ 消息通知系统 ⭐
- 卫生检查评分

### 5. 费用与数据可视化模块
- 水电费管理
- 住宿费缴纳记录
- ✅ ECharts数据统计（入住率、报修统计、费用分析）
- 数据可视化大屏

### 6. 性能优化与实时通信 ⭐
- ✅ Redis缓存配置
- ✅ WebSocket实时通信
- ✅ 消息队列（RabbitMQ）
- Token缓存机制
- 系统字典缓存
- 报表数据缓存

## 项目结构

```
SmartApartment/
├── frontend/                    # 前端项目（Vue 3）
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # 公共组件
│   │   ├── layouts/            # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # 状态管理（Pinia）
│   │   ├── utils/              # 工具函数
│   │   ├── views/              # 页面组件
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── index.html              # HTML模板
│   ├── package.json            # 前端依赖
│   └── vite.config.js          # Vite配置
├── src/                         # 后端项目（Spring Boot）
│   ├── main/
│   │   ├── java/org/smartapartment/smartapartment/
│   │   │   ├── common/          # 通用结果类
│   │   │   ├── config/          # 配置类（MyBatis、Redis、Knife4j等）
│   │   │   ├── controller/      # 控制器层
│   │   │   ├── entity/          # 实体类
│   │   │   ├── exception/       # 异常处理
│   │   │   ├── mapper/          # Mapper接口
│   │   │   ├── service/         # 业务逻辑层
│   │   │   └── util/            # 工具类
│   │   └── resources/
│   │       ├── application.properties  # 配置文件
│   │       └── mapper/          # MyBatis XML映射文件
│   └── test/                    # 测试类
├── sql/
│   └── smart_apartment.sql     # 数据库建表脚本
├── pom.xml                      # Maven依赖配置
├── README.md                    # 项目说明文档
├── 部署说明.md                 # 部署指南
├── API测试文档.md              # API测试文档
└── 开发规范.md                 # 开发规范
```

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 2. 数据库配置
```bash
# 创建数据库并导入SQL脚本
mysql -u root -p
source sql/smart_apartment.sql
```

### 3. 修改配置文件
编辑 `src/main/resources/application.properties`：
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/smart_apartment
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
```

### 4. 启动项目
```bash
# Maven方式
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/SmartApartment-0.0.1-SNAPSHOT.jar
```

### 5. 启动前端项目
```bash
cd frontend
npm install
npm run dev
```

### 6. 访问系统
- **前端地址**：http://localhost:3000
- **后端服务**：http://localhost:9090
- **接口文档**：http://localhost:9090/doc.html
- **默认账号**：admin / admin123

## API接口说明

### 认证管理
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出

### 楼宇管理
- `GET /api/building/page` - 分页查询楼宇列表
- `GET /api/building/{id}` - 根据ID查询楼宇
- `POST /api/building` - 新增楼宇
- `PUT /api/building` - 更新楼宇
- `DELETE /api/building/{id}` - 删除楼宇

### 房间管理
- `GET /api/room/page` - 分页查询房间列表
- `POST /api/room` - 新增房间
- `PUT /api/room` - 更新房间
- `DELETE /api/room/{id}` - 删除房间

### 入住管理
- `GET /api/checkin/page` - 分页查询入住申请
- `POST /api/checkin/submit` - 提交入住申请
- `POST /api/checkin/approve` - 审批入住申请
- `POST /api/checkin/confirm/{id}` - 确认入住

### 报修管理
- `GET /api/repair/page` - 分页查询报修列表
- `POST /api/repair/submit` - 提交报修申请
- `POST /api/repair/accept` - 维修人员接单
- `POST /api/repair/status` - 更新维修进度
- `POST /api/repair/rate` - 满意度评价

### 换宿管理 ⭐
- `GET /api/transfer/page` - 分页查询换宿申请
- `POST /api/transfer/apply` - 提交换宿申请
- `POST /api/transfer/approve` - 审批换宿申请

### 退宿管理 ⭐
- `GET /api/checkout/page` - 分页查询退宿申请
- `POST /api/checkout/apply` - 提交退宿申请
- `POST /api/checkout/approve` - 审批退宿申请

### 公告管理 ⭐
- `GET /api/announcement/page` - 分页查询公告列表
- `POST /api/announcement` - 新增公告
- `POST /api/announcement/publish/{id}` - 发布公告（触发推送）
- `POST /api/announcement/withdraw/{id}` - 撤回公告

### 消息通知 ⭐
- `GET /api/message/page` - 分页查询消息列表
- `POST /api/message/read/{id}` - 标记消息已读
- `POST /api/message/readAll` - 全部标记已读

### 数据统计
- `GET /api/statistics/dashboard` - 获取首页统计数据
- `GET /api/statistics/room-status` - 房间状态统计
- `GET /api/statistics/repair-type` - 报修类型统计
- `GET /api/statistics/fee` - 费用统计

## 数据库设计

### 核心表结构
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_menu` - 菜单表
- `building` - 楼宇表
- `room` - 房间表
- `bed` - 床位表
- `check_in_application` - 入住申请表
- `checkout_application` - 退宿申请表 ⭐
- `transfer_application` - 换宿申请表 ⭐
- `repair_application` - 报修申请表
- `announcement` - 公告表 ⭐
- `message` - 消息通知表 ⭐
- `hygiene_inspection` - 卫生检查表
- `fee_record` - 费用记录表
- `sys_log` - 操作日志表

详细表结构请查看 `sql/smart_apartment.sql`

## 开发进度

### 后端（Spring Boot）
- [x] 项目框架搭建
- [x] 数据库设计与建表（15张表）
- [x] 核心实体类创建（13个Entity）
- [x] 用户登录认证（JWT）
- [x] 楼宇资源管理（完整CRUD）
- [x] 房间床位管理
- [x] 入住申请流程（7个接口）
- [x] 换宿申请流程 ⭐
- [x] 退宿申请流程 ⭐
- [x] 报修服务功能（完整流程）
- [x] 公告管理系统 ⭐
- [x] WebSocket实时通信 ⭐
- [x] 消息通知系统 ⭐
- [x] 数据统计接口（ECharts）
- [x] Redis缓存配置
- [x] RabbitMQ消息队列 ⭐
- [x] 全局异常处理
- [x] 接口文档（Knife4j）
- [x] 60+个RESTful接口 ⭐

### 前端（Vue 3）
- [x] Vue3 + Vite项目框架
- [x] Element Plus UI集成
- [x] 路由配置（Vue Router）
- [x] 状态管理（Pinia）
- [x] 登录页面
- [x] 主布局框架（管理端+学生端）
- [x] 首页数据统计（ECharts图表）
- [x] 楼宇管理页面（完整CRUD）
- [x] 房间管理页面 ⭐
- [x] 用户管理页面 ⭐
- [x] 入住管理页面（管理端+学生端）
- [x] 换宿管理页面（管理端+学生端）⭐
- [x] 退宿管理页面（管理端+学生端）⭐
- [x] 报修管理页面（管理端+学生端）⭐
- [x] 公告管理页面 ⭐
- [x] WebSocket实时通知组件 ⭐
- [x] iOS风格通知弹窗 ⭐
- [x] 请求拦截器配置
- [x] 路由守卫
- [x] 16个页面全部完成 ⭐

### 文档
- [x] README项目说明
- [x] 部署说明文档
- [x] API测试文档
- [x] 开发规范文档
- [x] 前端README

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进项目！

## 许可证

本项目采用 Apache 2.0 开源协议

## 联系方式

- 作者：郭建新
- 邮箱：your-email@example.com
- 毕业设计项目 - 2024
