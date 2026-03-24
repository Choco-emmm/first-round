<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 标题区域 -->
      <div class="login-header">
        <h2>宿舍报修系统</h2>
        <p>Dormitory Repair Management System</p>
      </div>

      <!-- 1. 登录表单区域 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        class="login-form"
        size="large"
      >
        <el-form-item prop="userId">
          <el-input v-model="loginForm.userId" placeholder="请输入工号/学号" prefix-icon="User" clearable />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" class="w-100" @click="handleLogin">登 录</el-button>
        </el-form-item>

        <!-- 注册入口 -->
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="showRegisterDialog = true">立即注册</el-link>
        </div>
      </el-form>
    </div>

    <!-- 2. 注册弹窗区域 -->
    <el-dialog v-model="showRegisterDialog" title="用户注册" width="400px" :close-on-click-modal="false">
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="工号" prop="userId">
          <el-input v-model="registerForm.userId" placeholder="例如：3225004123" />
          <div class="form-tip">学生：3125/3225开头；管理员：0025开头</div>
        </el-form-item>
        <el-form-item label="姓名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegisterDialog = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/api/request' 

// 👇 修复点 1: 使用 'import type' 导入纯类型，解决 verbatimModuleSyntax 报错
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const registerLoading = ref(false)
const showRegisterDialog = ref(false)

// ================= 登录逻辑部分 =================
const loginFormRef = ref<FormInstance>()
const loginForm = reactive({ userId: '', password: '' })

const validateUserId = (rule: any, value: string, callback: any) => {
  if (!value) return callback(new Error('请输入工号'))
  const stuRegex = /^(3125|3225)\d{6}$/
  const adminRegex = /^0025\d{6}$/
  if (stuRegex.test(value) || adminRegex.test(value)) callback()
  else callback(new Error('格式错误：学生3125/3225+6位，管理员0025+6位'))
}

const loginRules = reactive<FormRules>({
  userId: [{ validator: validateUserId, trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      // 👇 修复点 2: 加上 ': any' 断言，告诉 TS 返回的是后端数据对象，而不是 AxiosResponse
      const res: any = await request.post('/user/login', loginForm)
      
      if (res.code === 1) {
        ElMessage.success('登录成功')
        const info = res.data
        localStorage.setItem('token', info.token)
        localStorage.setItem('userInfo', JSON.stringify(info))
        
        // 根据 nextStep 跳转
        if (info.nextStep === 'STU_BIND') router.push({ name: 'BindRoom' })
        else if (info.nextStep === 'STU_HOME') router.push({ name: 'StudentHome' })
        else if (info.nextStep === 'ADMIN_HOME') router.push({ name: 'AdminHome' })
        else router.push({ name: info.role === 'ADMIN' ? 'AdminHome' : 'StudentHome' })
      } else {
        ElMessage.error(res.msg || '登录失败')
      }
    } catch (e) { 
      ElMessage.error('网络错误') 
      console.error(e)
    }
    finally { loading.value = false }
  })
}

// ================= 注册逻辑部分 =================
const registerFormRef = ref<FormInstance>()
const registerForm = reactive({ userId: '', username: '', password: '', confirmPassword: '' })

const validateConfirm = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) callback(new Error('两次密码不一致'))
  else callback()
}

const registerRules = reactive<FormRules>({
  userId: [{ validator: validateUserId, trigger: 'blur' }],
  username: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirm, trigger: 'blur' }]
})

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    registerLoading.value = true
    try {
      const { confirmPassword, ...params } = registerForm
      // 👇 修复点 2: 同样加上 ': any'
      const res: any = await request.post('/user/register', params)
      
      if (res.code === 1) {
        ElMessage.success('注册成功，请登录')
        showRegisterDialog.value = false
        loginForm.userId = registerForm.userId
        loginForm.password = registerForm.password
      } else {
        ElMessage.error(res.msg || '注册失败')
      }
    } catch (e) { 
      ElMessage.error('注册请求失败') 
      console.error(e)
    }
    finally { registerLoading.value = false }
  })
}
</script>

<style scoped>
.login-container { display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-box { width: 400px; padding: 40px; background: #fff; border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.login-header { text-align: center; margin-bottom: 30px; }
.login-header h2 { color: #333; margin-bottom: 10px; }
.login-header p { color: #999; font-size: 14px; }
.w-100 { width: 100%; }
.login-footer { text-align: right; font-size: 14px; color: #666; margin-top: 10px; }
.form-tip { font-size: 12px; color: #999; line-height: 1.5; margin-top: 4px; }
</style>