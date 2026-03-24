import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import StudentHome from '@/views/StudentHome.vue'
import AdminHome from '@/views/AdminHome.vue' // 👈 1. 取消注释（确保 views 目录下有这个文件）

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: LoginView
    },
    {
        path: '/student-home',
        name: 'StudentHome',
        component: StudentHome,
        meta: { requiresAuth: true }
    },
    // 👈 2. 新增管理员首页路由
    {
        path: '/admin-home',
        name: 'AdminHome',
        component: AdminHome,
        meta: { requiresAuth: true } // 管理员页面同样需要登录认证
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 👇【重要】全局前置守卫：防止未登录访问其他页面
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    // 如果要去的地方需要认证 (meta.requiresAuth 为 true)
    if (to.meta.requiresAuth) {
        if (!token) {
            // 没 token，强制踢回登录页
            next({ name: 'Login' })
        } else {
            next() // 有 token，放行
        }
    } else {
        // 如果去的是登录页，且已经有 token，可以可选地跳回首页，防止重复登录
        if (to.name === 'Login' && token) {
            next({ name: 'StudentHome' }) // 或者根据角色判断跳哪个首页
        } else {
            next() // 其他情况直接放行
        }
    }
})

export default router