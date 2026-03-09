import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from '@/store/modules/user'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/index.vue'),
        meta: {requiresAuth: false}
    },
    // 管理员专属路由
    {
        path: '/admin',
        component: () => import('../components/Layout/index.vue'),
        redirect: '/admin/statistics',
        meta: {requiresAuth: true, role: 'admin'},
        children: [
            {
                path: 'statistics',
                name: 'AdminStatistics',
                component: () => import('../views/admin/statistics/index.vue'),
                meta: {title: '数据看板', icon: 'DataAnalysis', role: 'admin'}
            },
            {
                path: 'system/doctor',
                name: 'DoctorManage',
                component: () => import('../views/admin/system/doctor/index.vue'),
                meta: {title: '医生管理', icon: 'User', role: 'admin'}
            },
            {
                path: 'system/nurse',
                name: 'NurseManage',
                component: () => import('../views/admin/system/nurse/index.vue'),
                meta: {title: '护士管理', icon: 'Service', role: 'admin'}
            },
            {
                path: 'system/role',
                name: 'RoleManage',
                component: () => import('../views/admin/system/index.vue'),
                meta: {title: '角色管理', icon: 'Lock', role: 'admin'}
            },
            // 兼容旧路径重定向
            {
                path: 'doctor/appointment',
                redirect: '/doctor/appointment'
            },
            {
                path: 'doctor/diagnosis',
                redirect: '/doctor/diagnosis'
            },
            {
                path: 'nurse/queue',
                redirect: '/nurse/queue'
            },
            {
                path: 'nurse/prescription',
                redirect: '/nurse/prescription'
            }
        ]
    },
    // 医生专属路由（新增数据看板）
    {
        path: '/doctor',
        component: () => import('../components/Layout/index.vue'),
        redirect: '/doctor/statistics', // 默认跳转到数据看板
        meta: {requiresAuth: true, role: 'doctor'},
        children: [
            {
                path: 'statistics', // 医生数据看板
                name: 'DoctorStatistics',
                component: () => import('../views/admin/statistics/index.vue'), // 复用管理员页面
                meta: {title: '数据看板', icon: 'DataAnalysis', role: 'doctor'}
            },
            {
                path: 'appointment',
                name: 'DoctorAppointment',
                component: () => import('../views/doctor/appointment/index.vue'),
                meta: {title: '接诊列表', icon: 'List', role: 'doctor'}
            },
            {
                path: 'diagnosis',
                name: 'DoctorDiagnosis',
                component: () => import('../views/doctor/diagnosis/index.vue'),
                meta: {title: '接诊工作台', icon: 'Monitor', role: 'doctor'}
            }
        ]
    },
    // 护士专属路由（新增数据看板）
    {
        path: '/nurse',
        component: () => import('../components/Layout/index.vue'),
        redirect: '/nurse/statistics', // 默认跳转到数据看板
        meta: {requiresAuth: true, role: 'nurse'},
        children: [
            {
                path: 'statistics', // 护士数据看板
                name: 'NurseStatistics',
                component: () => import('../views/admin/statistics/index.vue'), // 复用管理员页面
                meta: {title: '数据看板', icon: 'DataAnalysis', role: 'nurse'}
            },
            {
                path: 'queue',
                name: 'NurseQueue',
                component: () => import('../views/nurse/queue/index.vue'),
                meta: {title: '排队叫号', icon: 'Microphone', role: 'nurse'}
            },
            {
                path: 'prescription',
                name: 'NursePrescription',
                component: () => import('../views/nurse/prescription/index.vue'),
                meta: {title: '处方处理', icon: 'Files', role: 'nurse'}
            }
        ]
    },
    {
        path: '/404',
        component: () => import('../views/error/404.vue'),
        meta: {requiresAuth: false}
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

// 路由守卫逻辑不变
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const hasToken = userStore.token
    const requiredAuth = to.meta.requiresAuth

    if (to.path === '/login') {
        if (hasToken) {
            const roleHomeMap = {
                admin: '/admin/statistics',
                doctor: '/doctor/statistics', // 医生默认跳数据看板
                nurse: '/nurse/statistics'    // 护士默认跳数据看板
            }
            next(roleHomeMap[userStore.role] || '/login')
        } else {
            next()
        }
        return
    }

    if (requiredAuth) {
        if (!hasToken) {
            next('/login')
        } else {
            const userRole = userStore.role
            const requiredRole = to.meta.role

            if (userRole === requiredRole) {
                next()
            } else {
                const roleHomeMap = {
                    admin: '/admin/statistics',
                    doctor: '/doctor/statistics',
                    nurse: '/nurse/statistics'
                }
                next(roleHomeMap[userRole] || '/login')
            }
        }
    } else {
        next()
    }
})

export default router