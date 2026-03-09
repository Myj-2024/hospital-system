// @/utils/auth.js 完整修复版
// Token 存储的 key（必须统一）
const TOKEN_KEY = 'token'

/**
 * 获取 Token
 * @returns {string|null}
 */
export function getToken() {
    return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置 Token
 * @param {string} token - 要存储的 Token
 */
export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除 Token
 */
export function removeToken() {
    localStorage.removeItem(TOKEN_KEY)
}