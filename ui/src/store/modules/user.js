import {defineStore} from 'pinia'
import {login} from '@/api/user'
import {getToken, setToken, removeToken} from '@/utils/auth'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: getToken(),
        name: '',
        role: '', // 新增：存储用户实际角色（admin/doctor/nurse）
        roles: []
    }),

    actions: {
        // 登录逻辑
        async login(userInfo) {
            try {
                const res = await login(userInfo)

                // 核心修改：
                // 1. 从后端返回结果中获取 token 和 role（需确保后端接口返回 role 字段）
                const responseData = typeof res.data === 'object' ? res.data : {}
                const token = responseData.token || res.data
                const role = responseData.role || userInfo.role // 兼容：优先取后端返回的角色，兜底前端选择的角色

                if (!token) {
                    throw new Error('登录返回的 Token 为空，请检查接口')
                }

                // 2. 存储 token 和角色
                this.token = token
                this.role = role // 保存用户角色
                setToken(token)
                // 可选：将角色存储到本地存储，防止页面刷新丢失
                localStorage.setItem('userRole', role)

                return Promise.resolve(res)
            } catch (error) {
                console.error('Login Store Error:', error)
                return Promise.reject(error)
            }
        },

        // 注销逻辑
        logout() {
            this.token = ''
            this.role = '' // 清空角色
            removeToken()
            localStorage.removeItem('userRole') // 清除本地角色
            localStorage.clear()
            sessionStorage.clear()
        },

        // 新增：初始化角色（页面刷新后恢复）
        initRole() {
            const localRole = localStorage.getItem('userRole')
            if (localRole) {
                this.role = localRole
            }
        }
    }
})