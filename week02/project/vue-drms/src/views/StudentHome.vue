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
          <el-table-column prop="type" label="类型" width="120" />
          <el-table-column prop="detail" label="详情描述" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status===0" type="danger">待处理</el-tag>
              <el-tag v-else-if="row.status===1" type="warning">处理中</el-tag>
              <el-tag v-else type="success">已完成</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleDetail(row.id)">查看详情</el-button>
              <el-button link type="danger" v-if="row.status === 0" @click="handleDelete(row.id)">撤销</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-dialog v-model="showAddDialog" title="申请报修" width="500px">
        <el-form :model="form" label-width="80px">
          <el-form-item label="报修类型">
            <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%;">
              <el-option label="水暖维修" value="水暖维修" />
              <el-option label="电力维修" value="电力维修" />
              <el-option label="家具门窗" value="家具门窗" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="问题描述">
            <el-input v-model="form.detail" type="textarea" :rows="3" />
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import NavBar from '@/components/NavBar.vue'

const tableData = ref([])
const showAddDialog = ref(false)
const uploadHeaders = { Authorization: localStorage.getItem('token') || '' }

const form = reactive({ type: '', detail: '', imgIds: [] as number[], stuName: '', buildingId: '', roomId: '' })

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
    form.type = ''; form.detail = ''; form.imgIds = []
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
  const res: any = await request.get(`/repairRecord?id=${id}`)
  if (res.code === 1) ElMessageBox.alert(res.data.detail || '暂无详细描述', '详情')
}

onMounted(() => loadData())
</script>