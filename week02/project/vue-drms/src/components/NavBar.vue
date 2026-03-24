<template>
  <div class="navbar">
    <div class="navbar-left">
      <span class="logo">🏠 宿舍报修系统</span>
      <!-- 可以根据角色显示不同菜单，这里简化处理 -->
      <el-menu mode="horizontal" :ellipsis="false" background-color="#545c64" text-color="#fff" active-text-color="#ffd04b">
        <el-menu-item index="home" @click="goHome">首页</el-menu-item>
        <el-menu-item index="repair" @click="goRepair">报修管理</el-menu-item>
        <el-menu-item index="profile">个人中心</el-menu-item>
      </el-menu>
    </div>

    <div class="navbar-right">
      <div class="user-info">
        <el-avatar :size="32" icon="User" />
        <span class="username">{{ userInfo.username }}</span>
        <span class="role-tag">{{ roleText }}</span>
      </div>
      
      <el-button type="danger" link @click="handleLogout">
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userInfo = ref<any>({})

// 获取本地存储的用户信息
onMounted(() => {
  const storedInfo = localStorage.getItem('userInfo')
  if (storedInfo) {
    userInfo.value = JSON.parse(storedInfo)
  } else {
    // 如果没有信息，强制跳回登录页（防止非法访问）
    router.push({ name: 'Login' })
  }
})

// 计算角色显示文本
const roleText = computed(() => {
  return userInfo.value.role === 'ADMIN' ? '管理员' : '学生'
})

// 跳转逻辑（根据实际路由名称调整）
const goHome = () => {
  const target = userInfo.value.role === 'ADMIN' ? 'AdminHome' : 'StudentHome'
  router.push({ name: target })
}

const goRepair = () => {
  // 假设有一个报修列表页
  ElMessage.info('报修管理功能开发中...')
}

// 🔥 核心：退出登录逻辑
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    // 1. 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 2. 提示成功
    ElMessage.success('已安全退出')
    
    // 3. 强制跳转回登录页
    router.push({ name: 'Login' })
    
    // 4. (可选) 如果使用了 Pinia/Vuex，这里还需要 store.clear()
  }).catch(() => {
    // 用户取消
  })
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #545c64;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  color: #ffd04b;
}

/* 覆盖 element-plus 菜单的一些默认样式以适配深色背景 */
:deep(.el-menu) {
  border-bottom: none !important;
  background-color: #545c64;
}
:deep(.el-menu-item) {
  color: #fff !important;
}
:deep(.el-menu-item:hover), :deep(.el-menu-item.is-active) {
  background-color: #434a50 !important;
  color: #ffd04b !important;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.username {
  font-weight: 500;
}

.role-tag {
  font-size: 12px;
  padding: 2px 6px;
  background: rgba(255,255,255,0.2);
  border-radius: 4px;
  color: #ffd04b;
}

.el-button--danger.is-link {
  color: #ffcccc;
}
.el-button--danger.is-link:hover {
  color: #fff;
}
</style>