import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus' // 引入提示框

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

// ✨ 升级版路由拦截器：加了角色判断
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const userInfoStr = localStorage.getItem('userInfo')
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null

    // 1. 没登录，且去的不是登录页 -> 踢回登录
    if (to.name !== 'Login' && !token) {
        ElMessage.warning('请先登录')
        return next({ name: 'Login' })
    }

    // 2. 已经登录了，进行角色越权拦截
    if (token && userInfo) {
        // 管理员想去学生页面 -> 拦截并踢回管理员主页
        if (to.path.startsWith('/student') && userInfo.role !== 1) {
            ElMessage.error('越权访问：您是管理员，无权访问学生页面')
            return next({ name: 'AdminHome' })
        }
        // 学生想去管理员页面 -> 拦截并踢回学生主页
        if (to.path.startsWith('/admin') && userInfo.role !== 2) {
            ElMessage.error('越权访问：您是学生，无权访问管理员页面')
            return next({ name: 'StudentHome' })
        }
    }

    // 3. 正常放行
    next()
})

export default router