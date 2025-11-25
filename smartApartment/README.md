# 智慧公寓管理系统 - 前端项目

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 新一代前端构建工具
- **Element Plus** - Vue 3 UI组件库
- **Vue Router** - 官方路由管理器
- **Pinia** - Vue 3 状态管理
- **Axios** - HTTP请求库
- **ECharts** - 数据可视化图表库

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

访问地址：http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

## 项目结构

```
frontend/
├── public/              # 静态资源
├── src/
│   ├── api/            # API接口
│   ├── assets/         # 资源文件
│   ├── components/     # 公共组件
│   ├── layouts/        # 布局组件
│   ├── router/         # 路由配置
│   ├── stores/         # 状态管理
│   ├── utils/          # 工具函数
│   ├── views/          # 页面组件
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html          # HTML模板
├── package.json        # 依赖配置
└── vite.config.js      # Vite配置
```

## 功能模块

### 已完成
- ✅ 登录页面
- ✅ 主布局框架
- ✅ 首页数据统计（ECharts图表）
- ✅ 楼宇管理（完整CRUD）
- ✅ 路由守卫
- ✅ 请求拦截器
- ✅ 状态管理

### 待开发
- ⏳ 房间管理
- ⏳ 入住管理
- ⏳ 报修管理
- ⏳ 用户管理
- ⏳ 费用管理

## 默认账号

- 用户名：admin
- 密码：admin123

## 开发说明

### 添加新页面

1. 在 `src/views/` 创建页面组件
2. 在 `src/router/index.js` 添加路由
3. 在 `src/api/` 创建对应的API文件

### 调用接口

```javascript
import { getBuildingPage } from '@/api/building'

const res = await getBuildingPage({ current: 1, size: 10 })
console.log(res.data)
```

### 状态管理

```javascript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
console.log(userStore.userInfo)
```

## 注意事项

1. 确保后端服务已启动（http://localhost:8080）
2. 接口代理配置在 `vite.config.js` 中
3. Token自动添加在请求头中
4. 登录过期会自动跳转到登录页

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

---

**提示**：这是一个完整的前后端分离项目，前端通过Axios调用后端RESTful API。
