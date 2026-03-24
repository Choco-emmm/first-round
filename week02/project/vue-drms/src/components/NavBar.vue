<template>
  <div class="nav-bar">
    <div class="logo">
      <h2>宿舍报修系统</h2>
    </div>
    <div class="user-menu">
      <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
          <el-avatar :size="32" icon="UserFilled" />
          <span class="username">{{ userInfo.username || '用户' }}</span>
          <el-icon class="el-icon--right"><arrow-down /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="info">个人信息</el-dropdown-item>
            <el-dropdown-item command="password">修改密码</el-dropdown-item>
            <el-dropdown-item divided command="logout" style="color: red;">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-dialog v-model="showInfoDialog" title="个人基本信息" width="400px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="姓名">{{ userDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="工号/学号">{{ userDetail.userId }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ userDetail.role === 'ADMIN' ? '管理员' : '学生' }}</el-descriptions-item>
        <el-descriptions-item v-if="userDetail.role !== 'ADMIN'" label="宿舍">
          {{ userDetail.buildingId }} 栋 {{ userDetail.roomId }} 室
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="showPassDialog" title="修改密码" width="400px">
      <el-form ref="passFormRef" :model="passForm" label-width="80px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="passForm.password" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPassDialog = false">取消</el-button>
        <el-button type="primary" :loading="passLoading" @click="submitPass">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const userInfo = ref<any>({})

const showInfoDialog = ref(false)
const userDetail = ref<any>({})

const showPassDialog = ref(false)
const passLoading = ref(false)
const passForm = reactive({ password: '' })

onMounted(() => {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
})

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', { type: 'warning' }).then(() => {
      localStorage.clear()
      router.push({ name: 'Login' })
      ElMessage.success('已退出登录')
    }).catch(() => {})
  } else if (command === 'info') {
    const res: any = await request.get('/user')
    if (res.code === 1) {
      userDetail.value = res.data
      showInfoDialog.value = true
    }
  } else if (command === 'password') {
    passForm.password = ''
    showPassDialog.value = true
  }
}

const submitPass = async () => {
  if (!passForm.password) return ElMessage.warning('密码不能为空')
  passLoading.value = true
  try {
    const res: any = await request.put('/user', { password: passForm.password })
    if (res.code === 1) {
      ElMessage.success('密码修改成功，请重新登录')
      showPassDialog.value = false
      localStorage.clear()
      router.push({ name: 'Login' })
    }
  } finally {
    passLoading.value = false
  }
}
</script>

<style scoped>
.nav-bar { display: flex; justify-content: space-between; align-items: center; height: 60px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.08); padding: 0 20px; margin-bottom: 20px;}
.logo h2 { margin: 0; color: #409eff; font-size: 22px; }
.el-dropdown-link { display: flex; align-items: center; cursor: pointer; color: #333; outline: none; }
.username { margin: 0 8px; font-weight: 500; }
</style>