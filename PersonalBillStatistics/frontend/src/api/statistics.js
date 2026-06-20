import request from './request'

export function getCategoryStats(params) {
  return request.get('/statistics/category', { params })
}

export function getMonthlyStats(params) {
  return request.get('/statistics/monthly', { params })
}

export function getMonthlyCompare(params) {
  return request.get('/statistics/monthly-compare', { params })
}
