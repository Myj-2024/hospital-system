// api/system/department.js
import request from '@/utils/request'

/**
 * 科室分页查询
 * @param {Object} params - 查询参数（页码、页大小、科室名称、科室描述、状态等）
 * @returns {Promise}
 */
export function getDepartmentPage(params) {
    return request({
        url: '/admin/system/department/page',
        method: 'get',
        params
    })
}

/**
 * 新增科室
 * @param {Object} data - 科室信息
 * @returns {Promise}
 */
export function addDepartment(data) {
    return request({
        url: '/admin/system/department/add',
        method: 'post',
        data
    })
}

/**
 * 编辑科室
 * @param {Object} data - 科室信息
 * @returns {Promise}
 */
export function editDepartment(data) {
    return request({
        url: '/admin/system/department/update',
        method: 'put',
        data
    })
}

/**
 * 修改科室状态
 * @param {Object} data - 包含id和status的对象
 * @returns {Promise}
 */
export function updateDepartmentStatus(data) {
    return request({
        url: '/admin/system/department/status',
        method: 'put',
        data: {
            id: data.id,
            status: data.status
        }
    })
}

/**
 * 删除科室（支持单个/批量）
 * @param {Object} data - 包含id或idList的对象
 * @returns {Promise}
 */
export function deleteDepartment(data) {
    return request({
        url: '/admin/system/department/delete',
        method: 'delete',
        data
    })
}

/**
 * 根据ID查询科室详情
 * @param {Number} id - 科室ID
 * @returns {Promise}
 */
export function getDepartmentById(id) {
    return request({
        url: `/admin/system/department/${id}`,
        method: 'get',
        params: { id }
    })
}
