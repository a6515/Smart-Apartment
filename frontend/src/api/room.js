import request from '@/utils/request'

// 分页查询房间列表
export const getRoomPage = (params) => {
  return request({
    url: '/room/page',
    method: 'get',
    params
  })
}

// 根据ID查询房间
export const getRoomById = (id) => {
  return request({
    url: `/room/${id}`,
    method: 'get'
  })
}

// 新增房间
export const addRoom = (data) => {
  return request({
    url: '/room',
    method: 'post',
    data
  })
}

// 更新房间
export const updateRoom = (data) => {
  return request({
    url: '/room',
    method: 'put',
    data
  })
}

// 删除房间
export const deleteRoom = (id) => {
  return request({
    url: `/room/${id}`,
    method: 'delete'
  })
}

// 获取可用房间列表
export const getAvailableRooms = (buildingId, roomType) => {
  return request({
    url: '/room/available',
    method: 'get',
    params: { buildingId, roomType }
  })
}
