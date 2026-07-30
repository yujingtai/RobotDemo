<template>
  <div>
    <h3>告警管理</h3>
    <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:160px; margin-bottom:12px" @change="() => fetchData()">
      <el-option label="待处理" value="OPEN" />
      <el-option label="已确认" value="ACKNOWLEDGED" />
      <el-option label="已解决" value="RESOLVED" />
      <el-option label="已关闭" value="CLOSED" />
    </el-select>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="alertType" label="类型" width="140" />
      <el-table-column label="级别" width="80">
        <template #default="{ row }">
          <el-tag :type="levelTag(row.level)" size="small">{{ row.level }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="source" label="来源" width="100" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">{{ { OPEN:'待处理', ACKNOWLEDGED:'已确认', RESOLVED:'已解决', CLOSED:'已关闭' }[row.status] }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAlerts } from '@/api/index.js'

const loading = ref(false)
const tableData = ref([])
const statusFilter = ref('')
const levelTag = (l) => ({ CRITICAL:'danger', ERROR:'danger', WARN:'warning', INFO:'info' })[l] || 'info'

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAlerts({ status: statusFilter.value })
    if (res.code === 200) tableData.value = res.data.records || []
  } finally { loading.value = false }
}

onMounted(fetchData)
</script>
