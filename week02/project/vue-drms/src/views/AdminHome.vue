<template>
  <div class="app-layout">
    <NavBar />
    <div class="page-wrapper">
      <div style="padding: 20px; margin: 0 auto;">
        <el-tabs type="border-card" v-loading="loading">
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

            <el-table 
              :data="repairList" 
              stripe 
              size="large"
              style="width: 100%; border-radius: 8px; overflow: hidden; border: 1px solid #ebeef5;"
              :header-cell-style="{ background: '#f8fafc', color: '#334155', fontWeight: 'bold' }"
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55" align="center" />
              <el-table-column prop="id" label="单号" min-width="80" align="center" />
              <el-table-column prop="stuName" label="申请人" min-width="120" align="center" />
              <el-table-column prop="buildingId" label="楼号" min-width="100" align="center" />
              <el-table-column prop="roomId" label="房号" min-width="100" align="center" />
              
              <el-table-column label="类型" min-width="120" align="center">
                <template #default="{ row }">
                  <span style="font-weight: 500; color: #475569;">{{ getTypeText(row.type) }}</span>
                </template>
              </el-table-column>
              
              <el-table-column label="状态" min-width="150" align="center">
                <template #default="{ row }">
                  <el-select v-model="row.status" @change="updateStatus(row.id, row.status)" size="default" style="width: 110px;">
                    <el-option label="待处理" :value="1" />
                    <el-option label="处理中" :value="2" />
                    <el-option label="已完成" :value="3" />
                  </el-select>
                </template>
              </el-table-column>
              
              <el-table-column label="操作" min-width="140" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" @click="handleDetail(row.id)" style="font-weight: bold;">详情</el-button>
                  <el-button link type="danger" @click="deleteSingle(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="系统操作日志">
            <div style="display: flex; justify-content: flex-end; margin-bottom: 15px;">
              <el-button type="primary" plain @click="loadLogs(true)">
                <el-icon style="margin-right: 4px;"><Refresh /></el-icon> 刷新日志
              </el-button>
            </div>
            
            <el-table 
              :data="logList" 
              stripe 
              size="large"
              style="width: 100%; border-radius: 8px; overflow: hidden; border: 1px solid #ebeef5;"
              :header-cell-style="{ background: '#f8fafc', color: '#334155', fontWeight: 'bold' }"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="operateUserId" label="操作人ID" min-width="100" align="center" />
              <el-table-column prop="className" label="类名" min-width="200" show-overflow-tooltip />
              <el-table-column prop="methodName" label="方法名" min-width="150" show-overflow-tooltip />
              <el-table-column prop="operateTime" label="时间" min-width="180" align="center" />
              <el-table-column prop="costTime" label="耗时(ms)" min-width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.costTime > 500 ? 'danger' : 'info'" effect="plain">
                    {{ row.costTime }} ms
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>

        <el-dialog v-model="showDetailDialog" title="报修单详情" width="500px" append-to-body>
          <div v-loading="detailLoading">
            
            <el-descriptions :column="2" border size="small" style="margin-bottom: 15px; border-radius: 4px; overflow: hidden;">
              <el-descriptions-item label="申请人" label-align="center" align="center">
                {{ detailData.stuName || '未知' }}
              </el-descriptions-item>
              
              <el-descriptions-item label="报修类型" label-align="center" align="center">
                {{ detailData.type ? getTypeText(detailData.type) : '未知' }}
              </el-descriptions-item>
              
              <el-descriptions-item label="宿舍栋号" label-align="center" align="center">
                {{ detailData.buildingId ? detailData.buildingId + ' 栋' : '未知' }}
              </el-descriptions-item>
              
              <el-descriptions-item label="宿舍房号" label-align="center" align="center">
                {{ detailData.roomId || '未知' }}
              </el-descriptions-item>

              <el-descriptions-item label="当前状态" label-align="center" align="center" :span="2">
                <el-tag v-if="detailData.status" :type="getStatusTag(detailData.status)" size="small" effect="dark">
                  {{ getStatusText(detailData.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <p style="line-height: 1.6; margin-bottom: 15px; padding: 10px; background: #f5f7fa; border-radius: 4px; border-left: 4px solid #409eff;">
              <strong>问题描述：</strong><br/>
              <span style="color: #606266; margin-top: 5px; display: inline-block;">
                {{ detailData.detail || '暂无详细描述' }}
              </span>
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
            <div v-else style="color: #999; margin-top: 10px; font-size: 14px;">
              <strong>报修图片：</strong>暂无上传图片
            </div>
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import NavBar from '@/components/NavBar.vue'
import { Refresh } from '@element-plus/icons-vue' 

const repairList = ref([])
const logList = ref([])
const selectedIds = ref<number[]>([])
const searchForm = reactive({ stuName: '', type: '', status: '' })

const loading = ref(false) // ✨ 新增：全局表格加载状态
const showDetailDialog = ref(false)
const detailLoading = ref(false)
const detailData = ref<any>({})

const getTypeText = (type: number) => ({ 1: '水电器', 2: '家具', 3: '其他' }[type] || '未知')
const getStatusText = (status: number) => ({ 1: '待处理', 2: '处理中', 3: '已完成' }[status] || '未知')
const getStatusTag = (status: number) => ({ 1: 'danger', 2: 'warning', 3: 'success' }[status] || 'info' as any)
const getImageUrl = (filename: string) => {
  if (!filename) return ''
  if (filename.startsWith('http')) return filename
  return 'http://localhost:8080/images/' + filename
}

const loadRepairs = async () => {
  loading.value = true // ✨ 请求开始，转圈
  try {
    const res: any = await request({ url: '/repairRecord/admin', method: 'GET', params: searchForm })
    if (res.code === 1) repairList.value = res.data || []
  } finally {
    loading.value = false // ✨ 请求结束，关闭转圈
  }
}

// 提取 boolean 参数用于判断是否弹出刷新成功的消息
const loadLogs = async (showMessage = false) => {
  loading.value = true // ✨ 请求开始，转圈
  try {
    const res: any = await request.get('/operateLog/admin')
    if (res.code === 1) {
      const data = res.data || []
      // 逆序排序并截取最新100条（核心防卡死机制）
      logList.value = data.sort((a: any, b: any) => {
        return new Date(b.operateTime).getTime() - new Date(a.operateTime).getTime()
      }).slice(0, 100)
      if (showMessage) ElMessage.success('日志已刷新')
    }
  } finally {
    loading.value = false // ✨ 请求结束，关闭转圈
  }
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

onMounted(async () => { 
  // 初始化页面时，并行发出两个请求，且共用 loading 状态
  loading.value = true
  try {
    await Promise.all([
      request({ url: '/repairRecord/admin', method: 'GET', params: searchForm }).then((res: any) => {
        if (res.code === 1) repairList.value = res.data || []
      }),
      request.get('/operateLog/admin').then((res: any) => {
        if (res.code === 1) {
          logList.value = (res.data || []).sort((a: any, b: any) => 
            new Date(b.operateTime).getTime() - new Date(a.operateTime).getTime()
          ).slice(0, 100)
        }
      })
    ])
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 整个页面的大容器，保证背景铺满 */
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 🚨 删除了进场动画 animation: slideUp */
.page-wrapper {
  padding: 0 40px 24px 40px; 
  flex: 1; 
}

/* 最基础、最干净的 Tabs 样式，没有任何 Hover 效果 */
.el-tabs {
  border: none !important;
  border-radius: 16px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04) !important;
  background: #ffffff;
  width: 100%; 
}

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
</style>