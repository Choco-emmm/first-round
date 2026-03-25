<template>
  <div class="login-container">
    <div class="glass-card animate-fade-in-up">
      <div class="header">
        <div class="logo-box">
          <el-icon><Monitor /></el-icon>
        </div>
        <h2>宿舍报修系统</h2>
        <p class="subtitle">{{ isLogin ? '欢迎回来，请登录您的账户' : '创建新账户，开启便捷校园生活' }}</p>
      </div>

      <el-form :model="form" class="auth-form" size="large">
        <el-form-item v-if="!isLogin" class="animate-item" style="animation-delay: 0.1s;">
          <el-input v-model="form.username" placeholder="请输入姓名" prefix-icon="User" />
        </el-form-item>
        <el-form-item class="animate-item" style="animation-delay: 0.2s;">
          <el-input v-model="form.userId" placeholder="请输入学号/工号" prefix-icon="Postcard" />
        </el-form-item>
        <el-form-item class="animate-item" style="animation-delay: 0.3s;">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" prefix-icon="Lock" />
        </el-form-item>

        <el-form-item v-if="!isLogin" class="animate-item" style="animation-delay: 0.4s;">
          <el-radio-group v-model="form.role">
            <el-radio-button :value="1">我是学生</el-radio-button>
            <el-radio-button :value="2">我是管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item class="animate-item" style="animation-delay: 0.5s; margin-top: 30px;">
          <el-button type="primary" :loading="loading" class="submit-btn" @click="submitAuth">
            {{ isLogin ? '立 即 登 录' : '注 册 账 号' }}
          </el-button>
        </el-form-item>
        
        <div class="toggle-text animate-item" style="animation-delay: 0.6s;">
          <span @click="toggleMode">
            {{ isLogin ? '还没有账号？点击注册' : '已有账号？返回登录' }}
          </span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Postcard, Monitor } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const isLogin = ref(true)
const loading = ref(false)

const form = reactive({ username: '', userId: '', password: '', role: 1 })

const toggleMode = () => {
  isLogin.value = !isLogin.value
  form.username = ''; form.userId = ''; form.password = ''
}

const submitAuth = async () => {
  if (!form.userId || !form.password) return ElMessage.warning('请填写完整信息')
  if (!isLogin.value && !form.username) return ElMessage.warning('请输入姓名')

  loading.value = true
  try {
    const url = isLogin.value ? '/user/login' : '/user/register'
    const res: any = await request.post(url, form)
    if (res.code === 1) {
      if (!isLogin.value) {
        ElMessage.success('注册成功，请登录')
        toggleMode()
      } else {
        ElMessage.success('登录成功')
        localStorage.setItem('token', res.data.token)
        // ✨ 修复：增加 res.data 作为兼容后备，确保能存入正确的信息
        localStorage.setItem('userInfo', JSON.stringify(res.data.user || res.data || form))
        
        const role = res.data.user?.role || res.data?.role || form.role        
        const nextStep = res.data.nextStep // 根据你后端的判断
        
        if (nextStep === 'STU_BIND') router.push({ name: 'BindRoom' })
        else if (role === 2) router.push({ name: 'AdminHome' })
        else router.push({ name: 'StudentHome' })
      }
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 动态渐变背景 */
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 毛玻璃卡片 */
.glass-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.header { text-align: center; margin-bottom: 35px; }
.logo-box {
  width: 56px; height: 56px;
  margin: 0 auto 15px;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 14px;
  display: flex; justify-content: center; align-items: center;
  color: white; font-size: 28px;
  box-shadow: 0 8px 16px rgba(64, 158, 255, 0.3);
}
.header h2 { margin: 0; font-size: 24px; color: #333; letter-spacing: 1px; }
.subtitle { color: #888; font-size: 14px; margin-top: 8px; }

/* 按钮及交互 */
.submit-btn {
  width: 100%;
  border-radius: 10px;
  font-size: 16px;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
  border: none;
  transition: transform 0.2s, box-shadow 0.2s;
}
.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
}

.toggle-text { text-align: center; font-size: 14px; color: #409eff; cursor: pointer; transition: color 0.3s; }
.toggle-text:hover { color: #66b1ff; text-decoration: underline; }

/* 🚀 进场动画组 */
.animate-fade-in-up {
  animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
.animate-item {
  opacity: 0;
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>