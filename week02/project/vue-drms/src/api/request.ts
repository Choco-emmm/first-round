import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080', // 你的后端地址
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
request.interceptors.response.use(
    response => {
        const res = response.data
        // 如果后端返回的 code 不是 1，视为业务错误
        if (res.code !== 1) {
            ElMessage.error(res.msg || '系统错误')
            return Promise.reject(new Error(res.msg || 'Error'))
        }
        return res
    },
    error => {
        ElMessage.error('网络请求失败')
        return Promise.reject(error)
    }
)

export default request