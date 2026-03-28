import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router' // 确保在文件顶部引入了 router

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})


// 请求拦截器：自动添加 Token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            // 修改 request.ts 的拦截器部分
            config.headers['token'] = token
            // 或者你的后端叫什么名字就写什么，比如 config.headers['Authorization']
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器：统一处理 Result 结构
// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 1) {
            // 后端用 200 状态码但 code 返回 401 的情况
            if (res.code === 401 || res.msg === 'NOT_LOGIN') {
                handleUnauthorized()
                return Promise.reject(new Error('未登录'))
            }
            ElMessage.error(res.msg || '系统错误')
            return Promise.reject(new Error(res.msg || 'Error'))
        }
        return res
    },
    error => {
        // 打印出来看看，如果是跨域拦截，error.response 通常是 undefined
        console.dir(error)

        // 兼容获取状态码：应对不同版本的 Axios 或是 CORS 丢失响应体的情况
        const status = error.response ? error.response.status : (error.status || null);

        // 如果明确是 401，或者错误信息里包含 401
        if (status === 401 || (error.message && error.message.includes('401'))) {
            handleUnauthorized()
        } else {
            ElMessage.error(error.response?.data?.msg || '登录过期或网络异常')
        }
        return Promise.reject(error)
    }
)

// ✨ 杀手锏：使用 Vue Router 无刷新跳转
function handleUnauthorized() {
    ElMessage.error('登录已过期或无权限，请重新登录')
    localStorage.clear()

    // 放弃 window.location.href，改用 router.push
    router.push({ name: 'Login' })
}

export default request