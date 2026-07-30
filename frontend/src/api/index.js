import api from '@/utils/request'

// 认证
export const login = (data) => api.post('/auth/login', data)

// 商品
export const getProducts = (params) => api.get('/products', { params })
export const createProduct = (data) => api.post('/products', data)
export const updateProduct = (id, data) => api.put(`/products/${id}`, data)
export const onShelf = (id) => api.put(`/products/${id}/on-shelf`)
export const offShelf = (id) => api.put(`/products/${id}/off-shelf`)

// 订单
export const getOrders = (params) => api.get('/orders', { params })
export const createOrder = (data) => api.post('/orders', data)
export const getOrder = (id) => api.get(`/orders/${id}`)
export const generateQrCode = (id) => api.post(`/orders/${id}/pay-qrcode`)
export const queryPayStatus = (id) => api.get(`/orders/${id}/pay-status`)
export const pollPay = (id) => api.post(`/orders/${id}/poll`)

// 库存
export const getInventory = () => api.get('/inventory')

// 任务
export const getTasks = (params) => api.get('/tasks', { params })

// 告警
export const getAlerts = (params) => api.get('/alerts', { params })

// 审计
export const getAuditLogs = (params) => api.get('/audit', { params })

// 用户
export const getUsers = () => api.get('/users')
