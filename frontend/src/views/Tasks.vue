<template>
  <div>
    <h3>任务监控</h3>
    <el-row :gutter="8" style="margin-bottom:12px">
      <el-col :span="4">
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="fetchData">
          <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
        </el-select>
      </el-col>
      <el-col :span="2">
        <el-button :icon="'Refresh'" circle @click="fetchData" :loading="loading" />
      </el-col>
    </el-row>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="taskNo" label="任务编号" width="200" />
      <el-table-column prop="taskType" label="类型" width="100">
        <template #default="{ row }">{{ { NAV:'导航', GRASP:'抓取', SPEECH:'语音', CHECKOUT:'结算', INSPECTION:'巡检', SAFETY:'安全' }[row.taskType] || row.taskType }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80" />
      <el-table-column prop="retryCount" label="重试" width="60" />
      <el-table-column prop="durationMs" label="耗时(ms)" width="100" />
      <el-table-column prop="failReason" label="失败原因" min-width="150" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTasks } from '@/api/index.js'

const loading = ref(false)
const tableData = ref([])
const statusFilter = ref('')
const statuses = ['CREATED','QUEUED','RUNNING','PAUSED','SUCCEEDED','FAILED','CANCELLED','MANUAL_REQUIRED']

const statusTag = (s) => ({ SUCCEEDED:'success', FAILED:'danger', RUNNING:'warning', PAUSED:'info', MANUAL_REQUIRED:'danger', CANCELLED:'info' })[s] || 'info'

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 50 }
    if (statusFilter.value) params.status = statusFilter.value
    const res = await getTasks(params)
    if (res.code === 200) tableData.value = res.data.records || []
  } finally { loading.value = false }
}

onMounted(fetchData)
</script>
