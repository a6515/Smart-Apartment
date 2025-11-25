import request from '@/utils/request'

// 登录
export const login = (username, password) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  })
}

// 登出
export const logout = (userId) => {
  return request({
    url: '/auth/logout',
    method: 'post',
    params: { userId }
  })
}

// 注册
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}
