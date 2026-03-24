<template>
  <div class="nav-bar">
    <div class="logo-wrapper">
      <div class="logo-icon-box">
        <el-icon><Monitor /></el-icon>
      </div>
      <span class="sys-title">宿舍报修系统</span>
      
      <el-tag
        v-if="userInfo.role"
        :type="userInfo.role === 2 ? 'danger' : 'primary'"
        :effect="userInfo.role === 2 ? 'dark' : 'light'"
        round
        class="role-tag"
      >
        <el-icon style="margin-right: 4px; vertical-align: middle;">
          <Avatar v-if="userInfo.role === 2" />
          <User v-else />
        </el-icon>
        <span style="vertical-align: middle;">{{ userInfo.role === 2 ? '管理员端' : '学生端' }}</span>
      </el-tag>
    </div>

    <div class="user-menu">
      <el-dropdown @command="handleCommand" trigger="click">
        <span class="el-dropdown-link">
          <el-avatar :size="36" style="background: #f0f2f5; color: #409eff;">
            <el-icon :size="20"><UserFilled /></el-icon>
          </el-avatar>
          <span class="username">{{ userInfo.username || '用户' }}</span>
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="info">
              <el-icon><Postcard /></el-icon>个人信息
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon>修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout" style="color: #f56c6c;">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

   <el-dialog v-model="showInfoDialog" title="个人基本信息" width="400px" center append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="姓名">
          <strong>{{ userDetail.username }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="工号/学号">{{ userDetail.userId }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="userDetail.role === 2 ? 'warning' : 'success'">
            {{ userDetail.role === 2 ? '系统管理员' : '在校学生' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="userDetail.role === 1" label="宿舍位置">
          <el-tag type="info">{{ userDetail.buildingId }} 栋</el-tag>
          <el-tag type="info" style="margin-left: 10px;">{{ userDetail.roomId }} 室</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="showPassDialog" title="修改账户密码" width="400px" append-to-body>
      <el-form ref="passFormRef" :model="passForm" label-width="80px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="passForm.password" type="password" show-password placeholder="请输入新密码" prefix-icon="Lock" />
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
// 引入一批好看的图标
import { ArrowDown, UserFilled, Monitor, Avatar, User, Postcard, Lock, SwitchButton } from '@element-plus/icons-vue'
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
    ElMessageBox.confirm('确定要退出当前账号吗?', '退出提示', { 
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning' 
    }).then(() => {
      localStorage.clear()
      router.push({ name: 'Login' })
      ElMessage.success('已成功退出登录')
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
/* 导航栏整体风格优化（加高、加间距） */
.nav-bar { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  height: 76px; /* ✨ 从 64px 增大到 76px */
  background: rgba(255, 255, 255, 0.8) !important; 
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); 
  padding: 0 40px; /* ✨ 两侧留白增加 */
  margin-bottom: 24px;
  position: sticky; 
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 16px; /* ✨ 间距变大 */
}

/* 渐变色图标框放大 */
.logo-icon-box {
  width: 42px; /* ✨ 放大 */
  height: 42px; 
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 24px; /* ✨ 图标放大 */
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.sys-title { 
  font-size: 22px; /* ✨ 字体变大 */
  font-weight: bold; 
  color: #2c3e50; 
  letter-spacing: 1.5px; 
}

.role-tag {
  margin-left: 10px;
  font-weight: bold;
  font-size: 14px;
  padding: 0 12px;
  height: 28px;
  line-height: 26px;
  letter-spacing: 1px;
}

.el-dropdown-link { 
  display: flex; 
  align-items: center; 
  cursor: pointer; 
  color: #333; 
  outline: none; 
  padding: 6px 12px;
  border-radius: 8px;
  transition: background-color 0.2s;
}
.el-dropdown-link:hover {
  background-color: #f0f2f5;
}

.username { 
  margin: 0 12px; 
  font-weight: 600; 
  font-size: 16px; /* ✨ 名字字体变大 */
}
</style>