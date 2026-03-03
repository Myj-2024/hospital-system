import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/index.vue')
    },
    {
        path: '/admin',
        component: () => import('../components/Layout/index.vue'),
        redirect: '/admin/statistics',
        children: [
            {
                path: '/admin/statistics',
                name: 'Statistics',
                component: () => import('../views/admin/statistics/index.vue'),
                meta: {title: '数据看板', icon: 'DataAnalysis'}
            },
            // 医生工作台模块 - 统一使用绝对路径确保匹配
            {
                path: '/admin/doctor/appointment',
                name: 'DoctorAppointment',
                component: () => import('../views/doctor/appointment/index.vue'),
                meta: {title: '接诊列表', icon: 'List'}
            },
            {
                path: '/admin/doctor/diagnosis',
                name: 'DoctorDiagnosis',
                component: () => import('../views/doctor/diagnosis/index.vue'),
                meta: {title: '接诊工作台', icon: 'Monitor'}
            },
            // 护士站模块
            {
                path: '/admin/nurse/queue',
                name: 'NurseQueue',
                component: () => import('../views/nurse/queue/index.vue'),
                meta: {title: '排队叫号', icon: 'Microphone'}
            },
            {
                path: '/admin/nurse/prescription',
                name: 'NursePrescription',
                component: () => import('../views/nurse/prescription/index.vue'),
                meta: {title: '处方处理', icon: 'Files'}
            },
            // 系统管理模块
            {
                path: '/admin/system/doctor',
                name: 'DoctorManage',
                component: () => import('../views/admin/system/doctor/index.vue'),
                meta: {title: '医生管理', icon: 'User'}
            },
            {
                path: '/admin/system/nurse',
                name: 'NurseManage',
                component: () => import('../views/admin/system/nurse/index.vue'),
                meta: {title: '护士管理', icon: 'Service'}
            },
            {
                path: '/admin/system/role',
                name: 'RoleManage',
                component: () => import('../views/admin/system/index.vue'),
                meta: {title: '角色管理', icon: 'Lock'}
            }
        ]
    },
    {
        path: '/404',
        component: () => import('../views/error/404.vue')
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/404'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router