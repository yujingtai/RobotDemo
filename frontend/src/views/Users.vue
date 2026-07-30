<template>
  <div>
    <h3>用户管理</h3>
    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="roleTag(row.role)" size="small">{{ { ADMIN:'管理员', OPERATOR:'运营', MAINTAINER:'维护' }[row.role] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers } from '@/api/index.js'

const loading = ref(false)
const tableData = ref([])

const roleTag = (r) => ({ ADMIN:'danger', OPERATOR:'warning', MAINTAINER:'success' })[r] || 'info'

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUsers()
    if (res.code === 200) tableData.value = res.data || []
  } finally { loading.value = false }
}

onMounted(fetchData)
</script>
