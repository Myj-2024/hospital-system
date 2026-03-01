import { defineStore } from 'pinia'
import { login } from '@/api/user' // 确保 api/user/index.js 导出了 login
import { getToken, setToken, removeToken } from '@/utils/auth'

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
                const response = await login(userInfo)
                // 假设后端返回结构为 { code: 200, data: { token: 'xxx' } }
                const { token } = response.data
                this.token = token
                setToken(token) // 调用 utils/auth.js 中的方法
                return Promise.resolve()
            } catch (error) {
                return Promise.reject(error)
            }
        },

        // 注销逻辑
        logout() {
            this.token = ''
            removeToken()
            // 如果有路由逻辑，可以在这里重置
        }
    }
})