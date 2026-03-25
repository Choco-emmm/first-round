<template>
  <div class="app-layout">
    <NavBar />
    <div class="page-wrapper">
      <div style="padding: 20px; margin: 0 auto;">
        <el-card v-loading="loading">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: bold;">我的报修记录</span>
              <el-button type="primary" @click="showAddDialog = true">申请报修</el-button>
            </div>
          </template>
          
          <el-table 
            :data="tableData" 
            stripe 
            size="large"
            style="width: 100%; border-radius: 12px; overflow: hidden;"
            :header-cell-style="{ background: '#f8fafc', color: '#334155', fontWeight: 'bold', fontSize: '15px', height: '54px' }"
            :row-style="{ height: '60px' }"
          >
            <el-table-column prop="id" label="单号" width="100" align="center" />
            <el-table-column label="报修类型" width="140">
              <template #default="{ row }">
                <span style="font-weight: 500; color: #475569;">{{ getTypeText(row.type) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="buildingId" label="楼号" width="100" align="center" />
            <el-table-column prop="roomId" label="房号" width="100" align="center" />
            <el-table-column label="处理状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" effect="light" round size="large" style="font-weight: bold;">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="150" align="center" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" style="font-weight: bold;" @click="handleDetail(row.id)">
                  <el-icon style="margin-right: 4px;"><View /></el-icon>查看详情
                </el-button>
                <el-button link type="danger" v-if="row.status === 1" @click="handleDelete(row.id)">
                  <el-icon style="margin-right: 4px;"><Delete /></el-icon>撤销
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无报修记录" :image-size="100"></el-empty>
            </template>
          </el-table>
        </el-card>

        <el-dialog v-model="showAddDialog" title="申请报修" width="500px" append-to-body>
          <el-form ref="repairFormRef" :model="form" :rules="rules" label-width="90px">
            
            <el-form-item label="申请人" prop="stuName">
              <el-input v-model="form.stuName" placeholder="请输入姓名（可代填）" />
            </el-form-item>
            <el-form-item label="宿舍栋号" prop="buildingId">
  <el-input v-model.number="form.buildingId" type="number" min="1" placeholder="例如：15" />
</el-form-item>
            
            <el-form-item label="宿舍房号" prop="roomId">
              <el-input v-model="form.roomId" maxlength="3" placeholder="例如：402" />
            </el-form-item>

            <el-form-item label="报修类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%;">
                <el-option label="水电器" :value="1" />
                <el-option label="家具" :value="2" />
                <el-option label="其他" :value="3" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="问题描述" prop="detail">
              <el-input v-model="form.detail" type="textarea" :rows="3" maxlength="250" show-word-limit placeholder="250字以内，必填" />
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
            <el-button @click="closeDialog">取消</el-button>
            <el-button type="primary" @click="submitRepair">提交</el-button>
          </template>
        </el-dialog>

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
import { Plus, View, Delete } from '@element-plus/icons-vue'
import request from '@/api/request'
import NavBar from '@/components/NavBar.vue'

// ==== 1. 变量与状态定义 ====
const tableData = ref([])
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const loading = ref(false)
const detailLoading = ref(false)
const detailData = ref<any>({})
const repairFormRef = ref() 
const uploadHeaders = { token: localStorage.getItem('token') || '' }

// ✨ 重点：buildingId 改为数字类型，roomId 保持字符串
const form = reactive({ 
  type: 1, 
  detail: '', 
  imgIds: [] as number[], 
  stuName: '', 
  buildingId: undefined as number | undefined, 
  roomId: '' 
})

// ✨ 加上了极其严谨的类型和正则校验
const rules = reactive({
  stuName: [{ required: true, message: '请输入申请人姓名', trigger: 'blur' }],
  buildingId: [
    { required: true, message: '请输入宿舍栋号', trigger: 'blur' },
    { type: 'number', message: '栋号必须是纯数字哦', trigger: 'blur' },
    // ✨ 新增这条：告诉底层校验引擎，数字最小必须是 1
    { type: 'number', min: 1, message: '栋号不能是负数或零哦', trigger: 'blur' }
  ],
  roomId: [
    { required: true, message: '请输入宿舍房号', trigger: 'blur' },
    { pattern: /^\d{3}$/, message: '房间号必须是3位数字格式', trigger: 'blur' } // 正则表达式：只能是3位纯数字
  ],
  detail: [
    { required: true, message: '请填写问题描述', trigger: 'blur' },
    { whitespace: true, message: '描述不能全为空格', trigger: 'blur' }
  ]
})

// ==== 2. 辅助函数 ====
const getTypeText = (type: number) => ({ 1: '水电器', 2: '家具', 3: '其他' }[type] || '未知')
const getStatusText = (status: number) => ({ 1: '待处理', 2: '处理中', 3: '已完成' }[status] || '未知')
const getStatusTag = (status: number) => ({ 1: 'danger', 2: 'warning', 3: 'success' }[status] || 'info' as any)
const getImageUrl = (filename: string) => {
  if (!filename) return ''
  if (filename.startsWith('http')) return filename
  return 'http://localhost:8080/images/' + filename
}

// ==== 3. 核心业务逻辑 ====
const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/repairRecord/student')
    if (res.code === 1) tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const uploadSuccess = (res: any) => {
  if (res.code === 1) form.imgIds.push(res.data)
}

// 清理弹窗逻辑
const closeDialog = () => {
  showAddDialog.value = false
  if (repairFormRef.value) repairFormRef.value.resetFields()
  form.imgIds = [] // 图片数组单独清空
}

const submitRepair = async () => {
  if (!repairFormRef.value) return
  
  await repairFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      // ✨ 核心修改点：去本地拿缓存信息
      const userInfoStr = localStorage.getItem('userInfo')
      const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {}
      
      // ✨ 不再覆盖 form.stuName 等数据！
      // 而是用结构语法，保留表单上填的楼栋、房号信息，额外外挂一个 studentId 给后端！
      const finalData = {
        ...form,
        studentId: userInfo.id // 取出当前登录人的 ID 发给后端
      }
      
      const res: any = await request.post('/repairRecord/student', finalData)
      if (res.code === 1) {
        ElMessage.success('报修成功')
        closeDialog() // 统一调用 closeDialog 清理表单
        loadData()
      } else {
        ElMessage.error(res.msg || '报修失败')
      }
    }
  })
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

<style scoped>
.app-layout { min-height: 100vh; display: flex; flex-direction: column; }
.page-wrapper { padding: 0 40px 24px 40px; flex: 1; }
.el-card { border: none !important; border-radius: 16px !important; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04) !important; background: #ffffff; width: 100%; }
.el-table { border-radius: 8px; overflow: hidden; width: 100%; }
.el-table th.el-table__cell { background-color: #f8fafc !important; color: #475569; font-weight: 600; }
</style>