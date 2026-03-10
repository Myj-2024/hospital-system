// api/system/doctor.js
import request from '@/utils/request'

/**
 * 医生账号分页查询
 * @param {Object} params - 查询参数（页码、页大小、关键词等）
 * @returns {Promise}
 */
export function getDoctorPage(params) {
    return request({
        url: '/admin/system/doctor/page',
        method: 'get',
        params
    })
}

/**
 * 新增医生账号
 * @param {Object} data - 医生账号信息
 * @returns {Promise}
 */
export function addDoctor(data) {
    return request({
        url: '/admin/system/doctor/add',
        method: 'post',
        data
    })
}

/**
 * 编辑医生账号
 * @param {Object} data - 医生账号信息
 * @returns {Promise}
 */
export function editDoctor(data) {
    return request({
        url: '/admin/system/doctor/update',
        method: 'put',
        data
    })
}

/**
 * 修改医生账号状态
 * @param {Object} data - 包含id和status的对象
 * @returns {Promise}
 */
export function updateDoctorStatus(data) {
    return request({
        url: '/admin/system/doctor/status', // 移除路径中的/:status
        method: 'post',
        data: {
            id: data.id,
            status: data.status // 只传这两个字段，和UserStatusDTO匹配
        }
    })
}

/**
 * 删除医生账号（支持单个/批量）
 * @param {Object} data - 包含id或idList的对象
 * @returns {Promise}
 */
export function deleteDoctor(data) {
    return request({
        url: '/admin/system/doctor/delete',
        method: 'delete',
        data
    })
}

/**
 * 根据ID查询医生详情
 * @param {Number} id - 医生ID
 * @returns {Promise}
 */
export function getDoctorById(id) {
    return request({
        url: `/admin/system/doctor/${id}`,
        method: 'get',
        params: { id }
    })
}