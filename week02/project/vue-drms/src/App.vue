<template>
  <router-view />
</template>

<style>
/* 全局基础重置 */
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background-color: #f4f7fb; 
  color: #2c3e50;
  -webkit-font-smoothing: antialiased;
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