<template>
  <div>
    <h3>告警管理</h3>
    <el-table :data="[]" border>
      <el-table-column prop="alertType" label="告警类型" />
      <el-table-column prop="level" label="级别" width="100" />
      <el-table-column prop="source" label="来源" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="createTime" label="时间" width="180" />
    </el-table>
  </div>
</template>
