<template>
  <div class="bind-container">
    <div class="glass-card animate-fade-in-up">
      <div class="header">
        <div class="logo-box">
          <el-icon><Monitor /></el-icon>
        </div>
        <h2>完善宿舍信息</h2>
        <p class="subtitle">初次登录请绑定您的宿舍位置</p>
      </div>

      <el-form :model="bindForm" class="auth-form" size="large">
        <el-form-item class="animate-item" style="animation-delay: 0.1s;">
          <el-input v-model.number="bindForm.buildingId" placeholder="请输入楼号，例如：12" prefix-icon="OfficeBuilding" />
        </el-form-item>
        
        <el-form-item class="animate-item" style="animation-delay: 0.2s;">
          <el-input v-model.number="bindForm.roomId" placeholder="请输入房号，例如：302" prefix-icon="House" />
        </el-form-item>

        <el-form-item class="animate-item" style="animation-delay: 0.3s; margin-top: 35px;">
          <el-button type="primary" :loading="loading" class="submit-btn" @click="handleBind">
            确 认 绑 定
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Monitor, OfficeBuilding, House } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const bindForm = reactive({ buildingId: '', roomId: '' })
// 控制加载动画
const loading = ref(false)

const handleBind = async () => {
  if (!bindForm.buildingId || !bindForm.roomId) return ElMessage.warning('请填写完整的楼号和房号')
  
  loading.value = true // 点击后立刻开始转圈圈
  try {
    const res: any = await request.put('/user/student', bindForm)
    if (res.code === 1) {
      ElMessage.success('宿舍绑定成功！')
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      userInfo.buildingId = bindForm.buildingId
      userInfo.roomId = bindForm.roomId
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      router.push({ name: 'StudentHome' })
    } else {
      ElMessage.error(res.msg || '绑定失败，请重试')
    }
  } finally {
    loading.value = false // 无论成功失败，请求结束就停止转圈
  }
}
</script>

<style scoped>
.bind-container {
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

.glass-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
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

.animate-fade-in-up { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
.animate-item { opacity: 0; animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>