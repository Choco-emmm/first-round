<template>
  <div class="home-container">
    <!-- 1. 引入导航栏 -->
    <NavBar />
    
    <!-- 2. 页面主要内容 -->
    <div class="main-content">
      <h1>欢迎回来，{{ userName }}！</h1>
      <p>这里是学生的主页内容...</p>
      <!-- 其他业务组件 -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '@/components/NavBar.vue' // 👈 引入刚才创建的组件

const userName = ref('')

onMounted(() => {
  const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
  userName.value = info.username || '用户'
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}
.main-content {
  padding: 20px;
}
</style>