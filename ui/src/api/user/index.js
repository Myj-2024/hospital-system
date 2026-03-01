import request from '@/api/request'

export function login(data) {
    return request({
        url: '/user/login', // 对应后端控制器的路径
        method: 'post',
        data
    })
}