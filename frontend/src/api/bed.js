import request from '@/utils/request'

// 获取房间的可用床位列表
export const getAvailableBeds = (roomId) => {
  return request({
    url: '/bed/available',
    method: 'get',
    params: { roomId }
  })
}
