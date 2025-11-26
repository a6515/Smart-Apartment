import request from '@/utils/request'

// 获取首页统计数据
export const getDashboardData = () => {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

// 获取房间状态统计
export const getRoomStatusStatistics = () => {
  return request({
    url: '/statistics/room-status',
    method: 'get'
  })
}

// 获取报修类型统计
export const getRepairTypeStatistics = () => {
  return request({
    url: '/statistics/repair-type',
    method: 'get'
  })
}
