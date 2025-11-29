import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { initWebSocket } from './utils/websocket'
// 引入iOS风格通知样式
import './styles/ios-notification.css'

const app = createApp(App)
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')

// 在应用挂载后初始化WebSocket连接
router.isReady().then(() => {
  // 确保在每次路由变化时检查用户登录状态
  router.beforeEach((to, from, next) => {
    // 立即尝试初始化WebSocket (如果用户已登录)
    setTimeout(() => {
      // 会在initWebSocket函数中检查用户是否登录
      console.log('尝试初始化WebSocket连接...')
      initWebSocket()
    }, 500)
    next()
  })
  
  // 应用启动时也尝试初始化一次
  console.log('应用启动，尝试初始化WebSocket连接...')
  initWebSocket()
})
