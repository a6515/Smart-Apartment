import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  
  // 安全地解析用户信息
  let parsedUserInfo = {}
  try {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo && storedUserInfo !== 'null' && storedUserInfo !== 'undefined') {
      parsedUserInfo = JSON.parse(storedUserInfo)
    }
  } catch (error) {
    console.error('解析用户信息失败:', error)
    parsedUserInfo = {}
  }
  const userInfo = ref(parsedUserInfo)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    logout
  }
})
