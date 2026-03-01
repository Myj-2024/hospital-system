// 定义存储的 Key
const TokenKey = 'Admin-Token'

// 获取 Token
export function getToken() {
    return localStorage.getItem(TokenKey)
}

// 设置 Token (这就是你报错缺失的导出)
export function setToken(token) {
    return localStorage.setItem(TokenKey, token)
}

// 移除 Token
export function removeToken() {
    return localStorage.removeItem(TokenKey)
}