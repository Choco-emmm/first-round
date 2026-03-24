<template>
  <div class="bind-container">
    <el-card class="bind-card">
      <template #header><span>🏠 完善宿舍信息</span></template>
      
      <el-alert title="首次登录，请绑定您的宿舍信息" type="info" show-icon class="mb-20" />

      <el-form ref="bindFormRef" :model="bindForm" :rules="bindRules" label-width="100px">
        <el-form-item label="宿舍楼号" prop="buildingId">
          <!-- 注意：el-input-number 绑定的是数字 -->
          <el-input-number v-model="bindForm.buildingId" :min="1" :max="99" placeholder="请输入楼号" style="width: 100%" />
        </el-form-item>

        <el-form-item label="房间号" prop="roomId">
          <el-input v-model="bindForm.roomId" placeholder="例如：205" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交绑定</el-button>
          <el-button @click="logout">退出登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 👇 修改点 1: 加上 'type' 关键字，告诉 TS 这些只是类型，不参与运行时代码
import type { FormInstance, FormRules } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const loading = ref(false)
const bindFormRef = ref<FormInstance>()

// 定义表单数据，注意 buildingId 可能是 undefined 初始值
const bindForm = reactive({
  buildingId: undefined as number | undefined,
  roomId: ''
})

const bindRules = reactive<FormRules>({
  buildingId: [{ required: true, message: '请输入楼号', trigger: 'change' }],
  roomId: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
})

const handleSubmit = async () => {
  if (!bindFormRef.value) return
  
  await bindFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      // 发送请求
      const res = await request.put('/user/student', bindForm)
      
      // 👇 修改点 2: 你的 request 拦截器通常返回的是 res.data (后端的具体数据)
      // 如果不确定，可以 console.log(res) 看看结构。
      // 假设你的拦截器已经做了 response => response.data 的处理：
      const responseData = res.data 
      
      if (responseData.code === 1) {
        ElMessage.success('绑定成功')
        // 更新本地存储的用户信息（可选，为了保持最新）
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        userInfo.buildingId = bindForm.buildingId
        userInfo.roomId = bindForm.roomId
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
        
        router.push({ name: 'StudentHome' })
      } else {
        ElMessage.error(responseData.msg || '绑定失败')
      }
    } catch (error: any) {
      // 如果请求直接报错（比如网络断了，或者拦截器没处理 error）
      console.error(error)
      // 尝试从错误对象中获取信息，或者直接提示
      const msg = error.response?.data?.msg || '请求失败，请稍后重试'
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  })
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push({ name: 'Login' })
}
</script>

<style scoped>
.bind-container { display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f5f7fa; }
.bind-card { width: 450px; }
.mb-20 { margin-bottom: 20px; }
</style>