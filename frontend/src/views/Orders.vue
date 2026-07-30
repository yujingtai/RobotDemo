<template>
  <div>
    <div class="toolbar">
      <h3>订单管理</h3>
      <el-button type="primary" @click="showCreateDialog">创建订单</el-button>
    </div>

    <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:160px; margin-bottom:12px" @change="() => fetchData()">
      <el-option label="待支付" value="PENDING" />
      <el-option label="支付中" value="PAYING" />
      <el-option label="已支付" value="PAID" />
      <el-option label="支付失败" value="FAILED" />
      <el-option label="已取消" value="CANCELLED" />
    </el-select>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="productName" label="商品" min-width="120" />
      <el-table-column prop="amount" label="商品金额" width="100" />
      <el-table-column prop="postage" label="邮费" width="80" />
      <el-table-column prop="totalAmount" label="总金额" width="100" />
      <el-table-column prop="mailNo" label="邮件号" width="160" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">{{ statusLabel(row.status) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <template v-if="row.status === 'PENDING'">
            <el-button size="small" type="primary" :loading="payingId === row.id" @click="handleGenerateQr(row)">
              生成二维码
            </el-button>
          </template>
          <template v-else-if="row.status === 'PAYING'">
            <el-button size="small" type="success" :loading="payingId === row.id" @click="handleQueryPay(row)">
              查询支付
            </el-button>
          </template>
          <el-tag v-else-if="row.status === 'PAID'" type="success">已支付</el-tag>
          <el-tag v-else type="info">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 二维码弹窗 -->
    <el-dialog v-model="qrVisible" title="支付二维码" width="400px">
      <div style="text-align:center">
        <p>订单号: {{ currentOrder?.orderNo }}</p>
        <p>金额: {{ currentOrder?.totalAmount }} 元</p>
        <p>支付流水号: {{ currentOrder?.payTradeNo }}</p>
        <div class="mock-qr">[Mock] 扫码支付<br/>流水号: {{ currentOrder?.payTradeNo }}</div>
        <el-button type="success" style="margin-top:16px" :loading="payingId === currentOrder?.id" @click="handleQueryPay(currentOrder)">
          模拟支付确认
        </el-button>
      </div>
    </el-dialog>

    <!-- 创建订单弹窗 -->
    <el-dialog v-model="createVisible" title="创建订单" width="480px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="商品ID"><el-input-number v-model="createForm.productId" :min="1" /></el-form-item>
        <el-form-item label="商品名称"><el-input v-model="createForm.productName" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="createForm.quantity" :min="1" /></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="createForm.amount" :precision="2" :min="0" /></el-form-item>
        <el-form-item label="收件人"><el-input v-model="createForm.receiveName" /></el-form-item>
        <el-form-item label="收件人电话"><el-input v-model="createForm.receivePhone" /></el-form-item>
        <el-form-item label="收件人地址"><el-input v-model="createForm.receiveAddress" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateOrder">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrders, getOrder, createOrder, generateQrCode, queryPayStatus } from '@/api/index.js'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const total = ref(0)
const statusFilter = ref('')
const payingId = ref(null)
const qrVisible = ref(false)
const currentOrder = ref(null)
const createVisible = ref(false)
const creating = ref(false)
const createForm = reactive({ productId: 1, productName: '文创邮册', quantity: 1, amount: 39.9, receiveName: '张三', receivePhone: '13800000000', receiveAddress: '北京市朝阳区建国路88号' })

const statusLabel = (s) => ({ PENDING: '待支付', PAYING: '支付中', PAID: '已支付', FAILED: '失败', CANCELLED: '已取消', TIMEOUT: '已超时', MANUAL_REQUIRED: '需人工' })[s] || s

const fetchData = async (p = 1) => {
  page.value = p
  loading.value = true
  try {
    const res = await getOrders({ page: page.value, size: 10, status: statusFilter.value })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } finally { loading.value = false }
}

const showCreateDialog = () => { createVisible.value = true }
const handleCreateOrder = async () => {
  creating.value = true
  try {
    const res = await createOrder(createForm)
    if (res.code === 200) {
      ElMessage.success('订单创建成功: ' + res.data.orderNo)
      createVisible.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '创建失败')
  } finally { creating.value = false }
}

const handleGenerateQr = async (row) => {
  payingId.value = row.id
  try {
    const res = await generateQrCode(row.id)
    if (res.code === 200) {
      // 重新获取订单获取最新的 payTradeNo
      const orderRes = await getOrder(row.id)
      if (orderRes.code === 200) {
        currentOrder.value = orderRes.data
      } else {
        currentOrder.value = { ...row, payQrUrl: res.data }
      }
      qrVisible.value = true
      fetchData()
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '生成失败')
  } finally { payingId.value = null }
}

const handleQueryPay = async (row) => {
  if (!row) return
  payingId.value = row.id
  try {
    const res = await queryPayStatus(row.id)
    if (res.code === 200) {
      const zfzt = res.data
      if (zfzt === '01') {
        ElMessage.success('支付成功!')
        qrVisible.value = false
      } else if (zfzt === '00') {
        ElMessage.info('支付处理中... (再次查询将成功)')
      } else {
        ElMessage.warning('支付结果: ' + zfzt)
      }
      fetchData()
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '查询失败')
  } finally { payingId.value = null }
}

onMounted(() => fetchData())
</script>

<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.mock-qr { width:200px; height:200px; background:#f0f0f0; margin:12px auto; display:flex; flex-direction:column; align-items:center; justify-content:center; border:2px dashed #ccc; font-size:13px; color:#666; }
</style>
