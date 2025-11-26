import request from '@/utils/request'

// 分页查询退宿申请列表
export const getCheckoutPage = (params) => {
  return request({
    url: '/checkout/page',
    method: 'get',
    params
  })
}

// 提交退宿申请
export const applyCheckout = (data) => {
  return request({
    url: '/checkout/apply',
    method: 'post',
    data
  })
}

// 审批退宿申请
export const approveCheckout = (data) => {
  return request({
    url: '/checkout/approve',
    method: 'post',
    data
  })
}
