import axios from 'axios'

// 创建axios实例
const service = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 接口基础路径
    timeout: 5000
})

// 请求拦截器（添加token）
service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器（统一处理错误）
service.interceptors.response.use(
    response => response.data,
    error => {
        console.error('接口请求错误：', error)
        return Promise.reject(error)
    }
)

export default service