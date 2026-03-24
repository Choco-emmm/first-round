import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue') },
        { path: '/bind-room', name: 'BindRoom', component: () => import('../views/BindRoomView.vue') },
        { path: '/student/home', name: 'StudentHome', component: () => import('../views/StudentHome.vue') },
        { path: '/admin/home', name: 'AdminHome', component: () => import('../views/AdminHome.vue') }
    ]
})

// 路由拦截器：没 token 自动回登录页
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.name !== 'Login' && !token) {
        next({ name: 'Login' })
    } else {
        next()
    }
})

export default router