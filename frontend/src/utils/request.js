import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 标记是否正在处理登录过期
let isHandlingExpiredLogin = false

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    // 调试日志 - 记录请求参数
    if (config.params) {
      console.log(`【请求拦截器】${config.url} 参数:`, JSON.stringify(config.params))
      
      // 确保数值类型参数正确传递
      if (config.params.floorNumber) {
        config.params.floorNumber = parseInt(config.params.floorNumber)
      }
      if (config.params.roomType) {
        config.params.roomType = parseInt(config.params.roomType)
      }
      if (config.params.buildingId) {
        config.params.buildingId = parseInt(config.params.buildingId)
      }
      
      console.log(`【请求拦截器】${config.url} 处理后参数:`, JSON.stringify(config.params))
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 处理登录过期
const handleExpiredLogin = () => {
  if (isHandlingExpiredLogin) return
  isHandlingExpiredLogin = true
  
  const userStore = useUserStore()
  userStore.logout()
  ElMessage.error('登录已过期，请重新登录')
  router.push('/')
  
  // 1秒后重置标志位
  setTimeout(() => {
    isHandlingExpiredLogin = false
  }, 1000)
}

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 检查是否是 token 过期或未登录
      if (res.code === 401 || res.message?.includes('登录') || res.message?.includes('token')) {
        handleExpiredLogin()
        return Promise.reject(new Error('登录已过期'))
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    // 处理 HTTP 401 状态码
    if (error.response?.status === 401) {
      handleExpiredLogin()
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
