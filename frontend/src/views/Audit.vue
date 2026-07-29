<template>
  <div>
    <h3>审计日志</h3>
    <el-table :data="[]" border>
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="operateType" label="操作类型" width="100" />
      <el-table-column prop="target" label="操作对象" />
      <el-table-column prop="result" label="结果" width="80" />
      <el-table-column prop="traceId" label="流水号" />
      <el-table-column prop="operateTime" label="时间" width="180" />
    </el-table>
  </div>
</template>
