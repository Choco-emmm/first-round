<template>
  <NavBar />
  <div class="page-wrapper">
    <div style="padding: 20px; margin: 0 auto;">
      <el-tabs type="border-card">
        <el-tab-pane label="报修单管理">
          <el-form :inline="true" :model="searchForm" style="margin-bottom: 15px;">
            <el-form-item label="姓名">
              <el-input 
                v-model="searchForm.stuName" 
                placeholder="模糊查询" 
                clearable 
                @input="loadRepairs"
                @clear="loadRepairs"
              />
            </el-form-item>
            
            <el-form-item label="类型">
              <el-select 
                v-model="searchForm.type" 
                clearable 
                placeholder="全部分类" 
                style="width: 120px;"
                @change="loadRepairs"
              >
                <el-option label="水电器" :value="1" />
                <el-option label="家具" :value="2" />
                <el-option label="其他" :value="3" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="状态">
              <el-select 
                v-model="searchForm.status" 
                clearable 
                placeholder="全部状态" 
                style="width: 120px;"
                @change="loadRepairs"
              >
                <el-option label="待处理" :value="1" />
                <el-option label="处理中" :value="2" />
                <el-option label="已完成" :value="3" />
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-button type="danger" @click="batchDelete">批量删除</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="repairList" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="单号" width="80" />
            <el-table-column prop="stuName" label="申请人" width="100" />
            <el-table-column prop="buildingId" label="楼号" width="80" />
            <el-table-column prop="roomId" label="房号" width="80" />
            <el-table-column label="类型">
              <template #default="{ row }">{{ getTypeText(row.type) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-select v-model="row.status" @change="updateStatus(row.id, row.status)" size="small">
                  <el-option label="待处理" :value="1" />
                  <el-option label="处理中" :value="2" />
                  <el-option label="已完成" :value="3" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleDetail(row.id)">详情</el-button>
                <el-button link type="danger" @click="deleteSingle(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="系统操作日志">
          <el-table :data="logList" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="operateUserId" label="操作人Id" width="100" />
            <el-table-column prop="className" label="类名" show-overflow-tooltip />
            <el-table-column prop="methodName" label="方法名" />
            <el-table-column prop="operateTime" label="时间" width="180"/>
            <el-table-column prop="costTime" label="耗时(ms)" width="80" />
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <el-dialog v-model="showDetailDialog" title="报修单详情" width="500px">
        <div v-loading="detailLoading">
          <p style="line-height: 1.6; margin-bottom: 15px; padding: 10px; background: #f5f7fa; border-radius: 4px;">
            <strong>问题描述：</strong><br/>
            {{ detailData.detail || '暂无详细描述' }}
          </p>
          
          <div v-if="detailData.imgUrls && detailData.imgUrls.length > 0">
            <strong>报修图片：</strong>
            <div style="display: flex; gap: 10px; margin-top: 10px; flex-wrap: wrap;">
              <el-image
                v-for="(filename, index) in detailData.imgUrls"
                :key="index"
                :src="getImageUrl(filename)"
                :preview-src-list="detailData.imgUrls.map(getImageUrl)"
                :initial-index="index"
                fit="cover"
                style="width: 100px; height: 100px; border-radius: 6px; border: 1px solid #eee; cursor: pointer;"
              />
            </div>
          </div>
          <div v-else style="color: #999; margin-top: 10px;">
            <strong>报修图片：</strong>暂无上传图片
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import NavBar from '@/components/NavBar.vue'

const repairList = ref([])
const logList = ref([])
const selectedIds = ref<number[]>([])
const searchForm = reactive({ stuName: '', type: '', status: '' })

const showDetailDialog = ref(false)
const detailLoading = ref(false)
const detailData = ref<any>({})

const getTypeText = (type: number) => ({ 1: '水电器', 2: '家具', 3: '其他' }[type] || '未知')

// ✨ 最新的极简版图片拼接逻辑 ✨
const getImageUrl = (filename: string) => {
  if (!filename) return ''
  if (filename.startsWith('http')) return filename
  // 直接加上后端的 /images/ 映射前缀
  return 'http://localhost:8080/images/' + filename
}

const loadRepairs = async () => {
  const res: any = await request({ url: '/repairRecord/admin', method: 'GET', params: searchForm })
  if (res.code === 1) repairList.value = res.data || []
}

const loadLogs = async () => {
  const res: any = await request.get('/operateLog/admin')
  if (res.code === 1) logList.value = res.data || []
}

const handleSelectionChange = (val: any[]) => { selectedIds.value = val.map(v => v.id) }

const updateStatus = async (id: number, status: number) => {
  const res: any = await request.put(`/repairRecord/admin?id=${id}&status=${status}`)
  if (res.code === 1) ElMessage.success('状态已更新')
}

const batchDelete = () => {
  if (selectedIds.value.length === 0) return ElMessage.warning('未选择任何数据')
  doDelete(selectedIds.value.join(','))
}

const deleteSingle = (id: number) => doDelete(id.toString())

const doDelete = (ids: string) => {
  ElMessageBox.confirm('确定删除选中数据?', '警告', { type: 'warning' }).then(async () => {
    const res: any = await request.delete(`/repairRecord?ids=${ids}`)
    if (res.code === 1) { ElMessage.success('删除成功'); loadRepairs() }
  }).catch(() => {})
}

const handleDetail = async (id: number) => {
  showDetailDialog.value = true
  detailLoading.value = true
  try {
    const res: any = await request.get(`/repairRecord?id=${id}`)
    if (res.code === 1) detailData.value = res.data || {}
    else ElMessage.error(res.msg || '获取详情失败')
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => { loadRepairs(); loadLogs() })
</script>

<style scoped>
/* 整个页面的大容器，保证背景铺满 */
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 页面主体进场动效与全宽布局 */
.page-wrapper {
  padding: 0 40px 24px 40px; /* 顶部0，左右40px（和导航栏对齐），底部24px */
  flex: 1; /* 让主体内容撑满剩下的屏幕高度 */
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both;
}

/* 高级悬浮卡片（取消原有的任何限制，让卡片自然撑满） */
.el-card, .el-tabs {
  border: none !important;
  border-radius: 16px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04) !important;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  width: 100%; /* 强制占据 100% 宽度 */
}
.el-card:hover, .el-tabs:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
}

/* 表格圆角与无缝处理 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
}
.el-table th.el-table__cell {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 600;
}

@keyframes slideUp {
  0% { opacity: 0; transform: translateY(20px); }
  100% { opacity: 1; transform: translateY(0); }
}
</style>