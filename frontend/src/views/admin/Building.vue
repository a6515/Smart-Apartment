<template>
  <div class="building-page">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼宇名称">
          <el-input v-model="searchForm.buildingName" placeholder="请输入楼宇名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="楼宇类型">
          <el-select v-model="searchForm.buildingType" placeholder="请选择" clearable style="width: 200px">
            <el-option label="男生宿舍" value="1" />
            <el-option label="女生宿舍" value="2" />
            <el-option label="混合宿舍" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" :icon="Search">查询</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
          <el-button type="success" @click="handleAdd" :icon="Plus">新增</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="buildingName" label="楼宇名称" />
        <el-table-column prop="buildingCode" label="楼宇编号" />
        <el-table-column prop="buildingType" label="楼宇类型">
          <template #default="{ row }">
            <el-tag v-if="row.buildingType === 1" type="primary">男生宿舍</el-tag>
            <el-tag v-else-if="row.buildingType === 2" type="danger">女生宿舍</el-tag>
            <el-tag v-else type="info">混合宿舍</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="floors" label="楼层数" />
        <el-table-column prop="totalRooms" label="房间总数" />
        <el-table-column prop="managerName" label="管理员" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="楼宇名称" prop="buildingName">
          <el-input v-model="formData.buildingName" />
        </el-form-item>
        <el-form-item label="楼宇编号" prop="buildingCode">
          <el-input v-model="formData.buildingCode" />
        </el-form-item>
        <el-form-item label="楼宇类型" prop="buildingType">
          <el-select v-model="formData.buildingType" style="width: 100%">
            <el-option label="男生宿舍" :value="1" />
            <el-option label="女生宿舍" :value="2" />
            <el-option label="混合宿舍" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="formData.floors" :min="1" />
        </el-form-item>
        <el-form-item label="房间总数" prop="totalRooms">
          <el-input-number v-model="formData.totalRooms" :min="1" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getBuildingPage, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增楼宇')
const submitLoading = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  buildingName: '',
  buildingType: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  buildingName: '',
  buildingCode: '',
  buildingType: 1,
  floors: 1,
  totalRooms: 1,
  address: '',
  description: ''
})

const formRules = {
  buildingName: [{ required: true, message: '请输入楼宇名称', trigger: 'blur' }],
  buildingCode: [{ required: true, message: '请输入楼宇编号', trigger: 'blur' }],
  buildingType: [{ required: true, message: '请选择楼宇类型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getBuildingPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.buildingName = ''
  searchForm.buildingType = ''
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增楼宇'
  Object.assign(formData, {
    id: null,
    buildingName: '',
    buildingCode: '',
    buildingType: 1,
    floors: 1,
    totalRooms: 1,
    address: '',
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑楼宇'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateBuilding(formData)
      ElMessage.success('更新成功')
    } else {
      await addBuilding(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该楼宇吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBuilding(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.building-page {
  padding: 0;
}
</style>
