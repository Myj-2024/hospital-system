import axios from 'axios'
import { ElMessage } from 'element-plus' // 假设你使用 Element Plus，用于弹出错误提示

// 创建axios实例
const service = axios.create({
    // 修改点：确保开发环境指向 http://localhost:8080，生产环境使用环境变量
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
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
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器（统一处理错误）
service.interceptors.response.use(
    response => {
        // 1. 获取后端返回的标准 Result 对象
        const res = response.data

        // 2. 逻辑判断：如果 code 不是 200，说明是业务逻辑错误（如密码错误、验证码过期）
        if (res.code !== 200) {
            // 弹出后端传回的 msg 提示
            ElMessage.error(res.msg || '系统异常')

            // 抛出异常，阻止代码进入 .then()
            return Promise.reject(new Error(res.msg || 'Error'))
        }

        // 3. 如果成功，直接返回 Result 里的 data 部分，方便前端直接使用
        return res
    },
    error => {
        // 处理 HTTP 状态码错误（如 403, 404, 500 等）
        console.error('接口请求错误：', error)

        let message = '网络连接异常'
        if (error.response && error.response.data) {
            // 尝试获取后端返回的错误信息
            message = error.response.data.msg || message
        } else if (error.message.includes('timeout')) {
            message = '请求超时，请稍后重试'
        }

        ElMessage.error(message)
        return Promise.reject(error)
    }
)

export default service