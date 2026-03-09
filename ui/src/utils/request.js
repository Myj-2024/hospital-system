import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

let isRefreshing = false

const service = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    timeout: 10000,
    // 新增：默认设置JSON请求头，确保POST数据以JSON格式传递
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

// 请求拦截器（强制打印日志，排查问题）
service.interceptors.request.use(
    config => {
        // 1. 强制打印：从 localStorage 取到的 token
        const token = localStorage.getItem('token')
        console.log('【请求拦截器】从 localStorage 取到的 token:', token)

        if (token) {
            // 2. 强制打印：要设置的 Authorization 头
            const authHeader = 'Bearer ' + token.trim()
            console.log('【请求拦截器】设置的 Authorization 头:', authHeader)

            // 3. 用最稳妥的方式设置请求头
            config.headers['Authorization'] = authHeader
        } else {
            console.warn('【请求拦截器】localStorage 中没有 token！')
        }

        // 新增：打印请求参数（重点！确认POST的data是否存在）
        console.log('【请求拦截器】请求URL:', config.url)
        console.log('【请求拦截器】请求方法:', config.method)
        console.log('【请求拦截器】GET参数(params):', config.params)
        console.log('【请求拦截器】POST数据(data):', config.data)

        // 4. 打印最终的请求头，确认是否设置成功
        console.log('【请求拦截器】最终请求头:', config.headers)

        return config
    },
    error => {
        console.error('【请求拦截器】请求配置错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        const res = response.data
        // 兼容后端可能的字段名（msg / message）
        if (res.code !== 200) {
            ElMessage.error(res.msg || res.message || '系统异常')
            return Promise.reject(new Error(res.msg || res.message || 'Error'))
        }
        return res
    },
    error => {
        console.error('【响应拦截器】接口请求错误:', error)
        console.error('【响应拦截器】错误详情:', {
            status: error.response?.status,
            data: error.response?.data,
            message: error.message
        })

        let message = '网络连接异常'
        if (error.response) {
            const status = error.response.status
            if (status === 401 && !isRefreshing) {
                isRefreshing = true
                message = '登录已过期，请重新登录'
                ElMessage.error(message)

                setTimeout(() => {
                    localStorage.removeItem('token')
                    router.push({ path: '/login', replace: true })
                    isRefreshing = false
                }, 800)
            } else if (error.response.data) {
                // 兼容后端msg/message字段
                message = error.response.data.msg || error.response.data.message || message
            }
        } else if (error.message.includes('timeout')) {
            message = '请求超时，请稍后重试'
        }

        if (error.response?.status !== 401) {
            ElMessage.error(message)
        }
        return Promise.reject(error)
    }
)

export default service