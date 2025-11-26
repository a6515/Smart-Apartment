import request from '@/utils/request'

// 分页查询入住申请列表
export const getCheckInPage = (params) => {
  return request({
    url: '/checkin/page',
    method: 'get',
    params
  })
}

// 提交入住申请
export const submitCheckIn = (data) => {
  return request({
    url: '/checkin/submit',
    method: 'post',
    data
  })
}

// 审批入住申请
export const approveCheckIn = (data) => {
  return request({
    url: '/checkin/approve',
    method: 'post',
    data
  })
}

// 确认入住
export const confirmCheckIn = (id) => {
  return request({
    url: `/checkin/confirm/${id}`,
    method: 'post'
  })
}

// 撤销申请（学生）
export const cancelCheckIn = (id) => {
  return request({
    url: `/checkin/cancel/${id}`,
    method: 'delete'
  })
}

// 删除申请（管理员）
export const deleteCheckIn = (id) => {
  return request({
    url: `/checkin/${id}`,
    method: 'delete'
  })
}

// 获取房间室友列表
export const getRoommates = (roomId) => {
  return request({
    url: `/checkin/roommates/${roomId}`,
    method: 'get'
  })
}
