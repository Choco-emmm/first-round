<template>
  <router-view v-slot="{ Component, route }">
    <transition name="page-fade" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<style>
/* 全局基础重置 */
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background-color: #f4f7fb; /* 更柔和的高级灰蓝背景 */
  color: #2c3e50;
  -webkit-font-smoothing: antialiased;
}

/* 🚀 页面切换动画核心逻辑 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(15px) scale(0.99);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-15px) scale(0.99);
}

/* 全局滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>