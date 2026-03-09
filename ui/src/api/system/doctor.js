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
        url: '/admin/system/doctor/edit',
        method: 'put',
        data
    })
}

/**
 * 删除/禁用医生账号
 * @param {Number} id - 医生ID
 * @returns {Promise}
 */
export function deleteDoctor(id) {
    return request({
        url: `/admin/system/doctor/${id}`,
        method: 'delete'
    })
}