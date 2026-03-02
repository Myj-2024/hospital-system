import request from '@/api/request' // 确认路径是否正确，指向你的 axios 实例

/**
 * 用户登录
 * 修复点：修改 url 路径，并确保 data 中包含 captcha
 */
export function login(data) {
    return request({
        // 核心修改：必须与后端 @RequestMapping("/admin/auth") + @PostMapping("/login") 一致
        url: '/admin/auth/login',
        method: 'post',
        data
    })
}

/**
 * 刷新 Token
 */
export function refreshToken() {
    return request({
        url: '/admin/auth/refresh',
        method: 'post'
    })
}