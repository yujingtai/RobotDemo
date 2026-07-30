<template>
  <div>
    <div class="toolbar">
      <h3>商品管理</h3>
      <el-button type="primary" @click="showCreateDialog">新增商品</el-button>
    </div>

    <el-row :gutter="8" style="margin-bottom:12px">
      <el-col :span="6"><el-input v-model="searchKeyword" placeholder="搜索商品名称" clearable @change="() => fetchData()" /></el-col>
      <el-col :span="4">
        <el-select v-model="searchStatus" placeholder="状态" clearable @change="() => fetchData()">
          <el-option label="上架" value="ON_SHELF" />
          <el-option label="下架" value="OFF_SHELF" />
        </el-select>
      </el-col>
    </el-row>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="商品名称" min-width="140" />
      <el-table-column prop="price" label="价格(元)" width="100" />
      <el-table-column prop="tags" label="标签" width="160" />
      <el-table-column prop="displayPosition" label="陈列点位" width="100" />
      <el-table-column label="可抓取" width="80">
        <template #default="{ row }">
          <el-tag :type="row.robotGrabbable ? 'success' : 'info'" size="small">
            {{ row.robotGrabbable ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ON_SHELF' ? 'success' : 'danger'" size="small">
            {{ row.status === 'ON_SHELF' ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editProduct(row)">编辑</el-button>
          <el-button v-if="row.status === 'ON_SHELF'" size="small" type="warning" @click="handleOffShelf(row)">下架</el-button>
          <el-button v-else size="small" type="success" @click="handleOnShelf(row)">上架</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" style="margin-top:16px; justify-content:center"
      :current-page="page" :page-size="size" :total="total"
      layout="total, prev, pager, next" @current-change="fetchData" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑商品' : '新增商品'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :precision="2" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="标签"><el-input v-model="form.tags" placeholder="用逗号分隔, 如: 邮册,纪念品" /></el-form-item>
        <el-form-item label="陈列点位"><el-input v-model="form.displayPosition" placeholder="如: A区-01" /></el-form-item>
        <el-form-item label="机器人抓取"><el-switch v-model="form.robotGrabbable" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProducts, createProduct, updateProduct, onShelf, offShelf } from '@/api/index.js'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const searchStatus = ref('')
const dialogVisible = ref(false)
const editId = ref(null)
const saving = ref(false)
const form = reactive({ name: '', price: 0, description: '', tags: '', displayPosition: '', robotGrabbable: 0 })

const fetchData = async (p = 1) => {
  page.value = p
  loading.value = true
  try {
    const res = await getProducts({ page, size: size.value, keyword: searchKeyword.value, status: searchStatus.value })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } finally { loading.value = false }
}

const showCreateDialog = () => {
  editId.value = null
  Object.assign(form, { name: '', price: 0, description: '', tags: '', displayPosition: '', robotGrabbable: 0 })
  dialogVisible.value = true
}

const editProduct = (row) => {
  editId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    const data = { ...form }
    if (editId.value) {
      await updateProduct(editId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createProduct(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData(page.value)
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  } finally { saving.value = false }
}

const handleOnShelf = async (row) => {
  await onShelf(row.id)
  ElMessage.success('已上架')
  fetchData(page.value)
}

const handleOffShelf = async (row) => {
  await offShelf(row.id)
  ElMessage.success('已下架')
  fetchData(page.value)
}

onMounted(() => fetchData())
</script>

<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
</style>
