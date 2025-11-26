import request from '@/utils/request'

// 分页查询换宿申请列表
export const getTransferPage = (params) => {
  return request({
    url: '/transfer/page',
    method: 'get',
    params
  })
}

// 提交换宿申请
export const applyTransfer = (data) => {
  return request({
    url: '/transfer/apply',
    method: 'post',
    data
  })
}

// 审批换宿申请
export const approveTransfer = (data) => {
  return request({
    url: '/transfer/approve',
    method: 'post',
    data
  })
}
