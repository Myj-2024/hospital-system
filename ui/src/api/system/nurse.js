// api/system/nurse.js（修复后）
import request from '@/utils/request'

export function getNursePage(params) {
    return request({
        url: '/admin/system/nurse/page',  // 新增 /admin/system 前缀，和医生接口保持一致
        method: 'get',
        params
    })
}

export function addNurse(data) {
    return request({
        url: '/admin/system/nurse/add',  // 新增前缀
        method: 'post',
        data
    })
}

export function editNurse(data) {
    return request({
        url: '/admin/system/nurse/update',  // 新增前缀
        method: 'put',
        data
    })
}

export function updateNurseStatus(data) {
    return request({
        url: '/admin/system/nurse/status',  // 新增前缀
        method: 'post',
        data: {
            id: data.id,
            status: data.status
        }
    })
}

export function deleteNurse(data) {
    return request({
        url: '/admin/system/nurse/delete',  // 新增前缀
        method: 'delete',
        data
    })
}

export function getNurseById(id) {
    return request({
        url: `/admin/system/nurse/${id}`,  // 新增前缀
        method: 'get',
        params: { id }
    })
}