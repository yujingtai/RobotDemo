<template>
  <div>
    <h3>审计日志</h3>
    <el-row :gutter="8" style="margin-bottom:12px">
      <el-col :span="4">
        <el-select v-model="typeFilter" placeholder="操作类型" clearable @change="() => fetchData()">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
      </el-col>
      <el-col :span="4">
        <el-input v-model="operatorFilter" placeholder="操作人" clearable @change="() => fetchData()" />
      </el-col>
    </el-row>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="operateType" label="操作类型" width="100" />
      <el-table-column prop="target" label="操作对象" width="160" />
      <el-table-column prop="result" label="结果" width="80">
        <template #default="{ row }"><el-tag :type="row.result==='SUCCESS'?'success':'danger'" size="small">{{ row.result }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="traceId" label="流水号" width="160" />
      <el-table-column prop="operateTime" label="时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAuditLogs } from '@/api/index.js'

const loading = ref(false)
const tableData = ref([])
const typeFilter = ref('')
const operatorFilter = ref('')
const types = ['LOGIN','CONFIG','ORDER','PAY','POSTAL','TASK','ALERT','USER']

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAuditLogs({ operateType: typeFilter.value, operator: operatorFilter.value })
    if (res.code === 200) tableData.value = res.data.records || []
  } finally { loading.value = false }
}

onMounted(fetchData)
</script>
