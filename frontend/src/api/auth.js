import request from '@/utils/request'

// 用户登录
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 用户登出
export const logout = (userId) => {
  return request({
    url: '/auth/logout',
    method: 'post',
    params: { userId }
  })
}
