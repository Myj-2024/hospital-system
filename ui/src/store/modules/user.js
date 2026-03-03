import {defineStore} from 'pinia'
import {login} from '@/api/user'
import {getToken, setToken, removeToken} from '@/utils/auth'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: getToken(),
        name: '',
        roles: []
    }),

    actions: {
        // 登录逻辑
        async login(userInfo) {
            try {
                const res = await login(userInfo)

                // 核心修正点：
                // 1. 根据你的拦截器 return res，这里的 res 就是 { code: 200, data: ..., msg: ... }
                // 2. 兼容两种常见的后端写法：data 是对象 { token: '...' } 或 data 直接是字符串 '...'
                const token = typeof res.data === 'object' ? res.data.token : res.data

                if (!token) {
                    throw new Error('登录返回的 Token 为空，请检查接口')
                }

                this.token = token
                setToken(token)

                return Promise.resolve(res)
            } catch (error) {
                console.error('Login Store Error:', error)
                return Promise.reject(error)
            }
        },

        // 注销逻辑
        logout() {
            this.token = ''
            removeToken()
            localStorage.clear() // 清除可能的残留
            sessionStorage.clear()
        }
    }
})