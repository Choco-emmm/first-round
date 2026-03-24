<template>
  <div>
    <NavBar />
    <div style="padding: 20px; max-width: 1000px; margin: 0 auto;">
      <el-card>
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold;">我的报修记录</span>
            <el-button type="primary" @click="showAddDialog = true">申请报修</el-button>
          </div>
        </template>
        
        <el-table :data="tableData" border stripe>
          <el-table-column prop="id" label="单号" width="80" />
          <el-table-column label="类型" width="120">
            <template #default="{ row }">{{ getTypeText(row.type) }}</template>
          </el-table-column>
          <el-table-column prop="buildingId" label="楼号" width="80" />
          <el-table-column prop="roomId" label="房号" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleDetail(row.id)">查看详情</el-button>
              <el-button link type="danger" v-if="row.status === 1" @click="handleDelete(row.id)">撤销</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-dialog v-model="showAddDialog" title="申请报修" width="500px">
        <el-form :model="form" label-width="80px">
          <el-form-item label="报修类型">
            <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%;">
              <el-option label="水电器" :value="1" />
              <el-option label="家具" :value="2" />
              <el-option label="其他" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="问题描述">
            <el-input v-model="form.detail" type="textarea" :rows="3" maxlength="250" show-word-limit placeholder="250字以内" />
          </el-form-item>
          <el-form-item label="上传图片">
            <el-upload
              action="http://localhost:8080/upload/student"
              :headers="uploadHeaders"
              name="file"
              :on-success="uploadSuccess"
              list-type="picture-card"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="submitRepair">提交</el-button>
        </template>
      </el-dialog>

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
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'
import NavBar from '@/components/NavBar.vue'

const tableData = ref([])
const showAddDialog = ref(false)
const uploadHeaders = { token: localStorage.getItem('token') || '' }

const form = reactive({ type: 1, detail: '', imgIds: [] as number[], stuName: '', buildingId: '', roomId: '' })

const showDetailDialog = ref(false)
const detailLoading = ref(false)
const detailData = ref<any>({})

const getTypeText = (type: number) => ({ 1: '水电器', 2: '家具', 3: '其他' }[type] || '未知')
const getStatusText = (status: number) => ({ 1: '待处理', 2: '处理中', 3: '已完成' }[status] || '未知')
const getStatusTag = (status: number) => ({ 1: 'danger', 2: 'warning', 3: 'success' }[status] || 'info' as any)

// ✨ 最新的极简版图片拼接逻辑 ✨
const getImageUrl = (filename: string) => {
  if (!filename) return ''
  if (filename.startsWith('http')) return filename
  // 直接加上后端的 /images/ 映射前缀
  return 'http://localhost:8080/images/' + filename
}

const loadData = async () => {
  const res: any = await request.get('/repairRecord/student')
  if (res.code === 1) tableData.value = res.data || []
}

const uploadSuccess = (res: any) => {
  if (res.code === 1) form.imgIds.push(res.data)
}

const submitRepair = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  form.stuName = userInfo.username
  form.buildingId = userInfo.buildingId
  form.roomId = userInfo.roomId
  const res: any = await request.post('/repairRecord/student', form)
  if (res.code === 1) {
    ElMessage.success('报修成功')
    showAddDialog.value = false
    form.detail = ''; form.imgIds = []
    loadData()
  }
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要撤销吗?', '提示', { type: 'warning' }).then(async () => {
    const res: any = await request.delete(`/repairRecord?ids=${id}`)
    if (res.code === 1) { ElMessage.success('撤销成功'); loadData() }
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

onMounted(() => loadData())
</script>