<template>
  <div>
    <NavBar />
    <div style="padding: 20px; max-width: 1200px; margin: 0 auto;">
      <el-tabs type="border-card">
        <el-tab-pane label="报修单管理">
          <el-form :inline="true" :model="searchForm" style="margin-bottom: 15px;">
            <el-form-item label="姓名"><el-input v-model="searchForm.stuName" placeholder="模糊查询" clearable /></el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" clearable placeholder="全部状态">
                <el-option label="待处理" :value="0" />
                <el-option label="处理中" :value="1" />
                <el-option label="已完成" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadRepairs">查询</el-button>
              <el-button type="danger" @click="batchDelete">批量删除</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="repairList" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="单号" width="80" />
            <el-table-column prop="stuName" label="申请人" width="100" />
            <el-table-column prop="buildingId" label="楼号" width="80" />
            <el-table-column prop="roomId" label="房号" width="80" />
            <el-table-column prop="type" label="类型" />
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-select v-model="row.status" @change="updateStatus(row.id, row.status)" size="small">
                  <el-option label="待处理" :value="0" />
                  <el-option label="处理中" :value="1" />
                  <el-option label="已完成" :value="2" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button link type="danger" @click="deleteSingle(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="系统操作日志">
          <el-table :data="logList" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="operateUser" label="操作人" width="100" />
            <el-table-column prop="className" label="类名" show-overflow-tooltip />
            <el-table-column prop="methodName" label="方法名" />
            <el-table-column prop="operateTime" label="时间" width="180"/>
            <el-table-column prop="costTime" label="耗时(ms)" width="80" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
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
const searchForm = reactive({ stuName: '', status: '' })

// 注意：后端的 list 接口如果是 POST，且使用 @RequestBody，要用 request.post
const loadRepairs = async () => {
  // 按照你的 Controller，应该是 GET /repairRecord/admin，但带 @RequestBody 在标准 HTTP 中是不规范的，这里假设你用 Axios 的 data 或者直接将对象作为请求体传过去
  const res: any = await request({ url: '/repairRecord/admin', method: 'GET', data: searchForm })
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

onMounted(() => { loadRepairs(); loadLogs() })
</script>