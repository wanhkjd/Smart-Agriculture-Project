import request from './request'

export function getCategories(type) {
  return request.get('/categories', { params: { type } })
}
