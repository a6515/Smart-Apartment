import request from '@/utils/request'

// 分页查询用户列表
export const getUserPage = (params) => {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

// 根据ID查询用户
export const getUserById = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

// 新增用户
export const addUser = (data) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

// 更新用户
export const updateUser = (data) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

// 删除用户
export const deleteUser = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 重置密码
export const resetPassword = (id) => {
  return request({
    url: `/user/reset-password/${id}`,
    method: 'post'
  })
}

// 修改用户状态
export const updateUserStatus = (id, status) => {
  return request({
    url: '/user/status',
    method: 'post',
    params: { id, status }
  })
}

// 更新用户个人信息
export const updateUserInfo = (data) => {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

// 修改密码
export const changePassword = (data) => {
  return request({
    url: '/user/info/change-password',
    method: 'post',
    data
  })
}
