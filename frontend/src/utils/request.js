import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (res) => {
    const data = res.data
    // 业务状态码非200 → 自动弹窗错误提示
    if (data && typeof data.code === 'number' && data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (err) => {
    // HTTP 错误 (4xx/5xx)
    const msg = err.response?.data?.message || err.message || '网络错误'
    const isLoginRequest = err.config?.url === '/auth/login'

    if (err.response?.status === 401) {
      if (isLoginRequest) {
        // 登录请求的401 = 密码错误, 只弹业务提示
        ElMessage.error(msg)
      } else {
        // 其他401 = token过期
        ElMessage.error('登录已过期，请重新登录')
        localStorage.clear()
        window.location.href = '/login'
      }
    } else {
      ElMessage.error(msg)
    }
    return Promise.reject(err)
  },
)

export default api
