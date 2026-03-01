import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件（先创建空组件，后续补充）
import Login from '../views/login/index.vue'
import PatientLayout from '../components/Layout/index.vue'
import NotFound from '../views/error/404.vue'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/patient',
        name: 'Patient',
        component: PatientLayout,
        children: [
            { path: 'appointment', component: () => import('../views/patient/appointment/index.vue') },
            { path: 'queue', component: () => import('../views/patient/queue/index.vue') },
            { path: 'payment', component: () => import('../views/patient/payment/index.vue') },
            { path: 'medical', component: () => import('../views/patient/medical/index.vue') }
        ]
    },
    {
        path: '/404',
        component: NotFound
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