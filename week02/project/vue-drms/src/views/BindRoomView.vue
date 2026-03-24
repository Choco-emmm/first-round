<template>
  <div class="bind-container">
    <el-card class="bind-box">
      <template #header>
        <div class="card-header">
          <h2>完善宿舍信息</h2>
          <span style="color: #999; font-size: 14px;">初次登录请绑定您的宿舍信息</span>
        </div>
      </template>
      <el-form :model="bindForm" label-width="80px" size="large">
        <el-form-item label="楼号">
          <el-input v-model.number="bindForm.buildingId" placeholder="例如：12" />
        </el-form-item>
        <el-form-item label="房号">
          <el-input v-model.number="bindForm.roomId" placeholder="例如：302" />
        </el-form-item>
        <el-button type="primary" style="width: 100%;" @click="handleBind">提交绑定</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const bindForm = reactive({ buildingId: '', roomId: '' })

const handleBind = async () => {
  if (!bindForm.buildingId || !bindForm.roomId) return ElMessage.warning('请填写完整')
  const res: any = await request.put('/user/student', bindForm)
  if (res.code === 1) {
    ElMessage.success('绑定成功')
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    userInfo.buildingId = bindForm.buildingId
    userInfo.roomId = bindForm.roomId
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    router.push({ name: 'StudentHome' })
  }
}
</script>
<style scoped>
.bind-container { display: flex; justify-content: center; align-items: center; height: 100vh; background: #f5f7fa; }
.bind-box { width: 400px; }
.card-header h2 { margin: 0 0 5px 0; }
</style>