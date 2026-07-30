<template>
  <div>
    <h3>库存管理</h3>
    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="productName" label="商品名称" min-width="140" />
      <el-table-column prop="productId" label="商品ID" width="70" />
      <el-table-column prop="totalQuantity" label="总库存" width="100" />
      <el-table-column prop="lockedQuantity" label="锁定库存" width="100" />
      <el-table-column prop="availableQuantity" label="可用库存" width="100" />
      <el-table-column prop="lowThreshold" label="低库存阈值" width="120" />
      <el-table-column label="状态" width="180">
        <template #default="{ row }">
          <el-tag v-if="row.availableQuantity <= row.lowThreshold" type="danger" size="small">低库存!</el-tag>
          <el-tag v-else type="success" size="small">正常</el-tag>
          <el-tag v-if="row.sampleMissing" type="warning" size="small" style="margin-left:4px">样品缺失</el-tag>
          <el-tag v-if="row.sampleMisplaced" type="info" size="small" style="margin-left:4px">陈列错位</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getInventory } from '@/api/index.js'

const loading = ref(false)
const tableData = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getInventory()
    if (res.code === 200) tableData.value = res.data || []
  } finally { loading.value = false }
}

onMounted(fetchData)
</script>
