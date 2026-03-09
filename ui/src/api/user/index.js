import request from '@/utils/request.js'

/**
 * 获取验证码
 */
export function getCaptcha() {
    return request({
        url: '/captcha',
        method: 'get'
    })
}

/**
 * 用户登录
 */
export function login(data) {
    return request({
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