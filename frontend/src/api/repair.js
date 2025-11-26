import request from '@/utils/request'

// 分页查询报修列表
export const getRepairPage = (params) => {
  return request({
    url: '/repair/page',
    method: 'get',
    params,
    paramsSerializer: (params) => {
      const searchParams = new URLSearchParams()
      Object.keys(params).forEach(key => {
        const value = params[key]
        if (Array.isArray(value)) {
          // 数组参数：repairTypes=1&repairTypes=2
          value.forEach(item => searchParams.append(key, item))
        } else if (value !== null && value !== undefined && value !== '') {
          searchParams.append(key, value)
        }
      })
      return searchParams.toString()
    }
  })
}

// 提交报修
export const submitRepair = (data) => {
  return request({
    url: '/repair/submit',
    method: 'post',
    data
  })
}

// 接单
export const acceptRepair = (data) => {
  return request({
    url: '/repair/accept',
    method: 'post',
    data
  })
}

// 更新状态
export const updateRepairStatus = (data) => {
  return request({
    url: '/repair/status',
    method: 'post',
    data
  })
}

// 评价
export const rateRepair = (data) => {
  return request({
    url: '/repair/rate',
    method: 'post',
    data
  })
}
