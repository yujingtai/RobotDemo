<template>
  <div>
    <h3>任务监控</h3>
    <el-table :data="[]" border>
      <el-table-column prop="taskNo" label="任务编号" />
      <el-table-column prop="taskType" label="类型" width="100" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="priority" label="优先级" width="80" />
      <el-table-column prop="retryCount" label="重试次数" width="100" />
      <el-table-column prop="durationMs" label="耗时(ms)" width="100" />
      <el-table-column prop="failReason" label="失败原因" />
    </el-table>
  </div>
</template>
