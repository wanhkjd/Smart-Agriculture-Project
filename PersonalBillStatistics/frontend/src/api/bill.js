import request from './request'

export function getBills(params) {
  return request.get('/bills', { params })
}

export function createBill(data) {
  return request.post('/bills', data)
}

export function updateBill(id, data) {
  return request.put(`/bills/${id}`, data)
}

export function deleteBill(id) {
  return request.delete(`/bills/${id}`)
}
