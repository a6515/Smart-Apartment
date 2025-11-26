import request from '@/utils/request'

// 分页查询楼宇列表
export const getBuildingPage = (params) => {
  return request({
    url: '/building/page',
    method: 'get',
    params
  })
}

// 根据ID查询楼宇
export const getBuildingById = (id) => {
  return request({
    url: `/building/${id}`,
    method: 'get'
  })
}

// 新增楼宇
export const addBuilding = (data) => {
  return request({
    url: '/building',
    method: 'post',
    data
  })
}

// 更新楼宇
export const updateBuilding = (data) => {
  return request({
    url: '/building',
    method: 'put',
    data
  })
}

// 删除楼宇
export const deleteBuilding = (id) => {
  return request({
    url: `/building/${id}`,
    method: 'delete'
  })
}

// 获取所有楼宇列表（不分页）
export const getBuildingList = () => {
  return request({
    url: '/building/list',
    method: 'get'
  })
}
