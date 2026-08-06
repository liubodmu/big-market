<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRaffleStore } from '@/stores/raffle'
import { apiSkuList } from '@/api/activity'
import { apiCredit, apiCreditPayExchangeSku } from '@/api/credit'
import SkuCard from '@/components/SkuCard.vue'
import type { SkuProductResponseDTO } from '@/types/api'
import { formatAmount, toCents } from '@/utils/format'

const user = useUserStore()
const raffle = useRaffleStore()

const skuList = ref<SkuProductResponseDTO[]>([])
const loading = ref(false)
const exchanging = ref(false)

async function load() {
  loading.value = true
  try {
    const [skus, credit] = await Promise.all([apiSkuList(user.activityId), apiCredit(user.userId)])
    skuList.value = skus
    user.setCredit(credit)
  } finally {
    loading.value = false
  }
}

async function onExchange(sku: SkuProductResponseDTO) {
  const cost = formatAmount(sku.productAmount)
  if (Number(sku.productAmount) > user.credit) {
    ElMessage.warning('积分余额不足')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认消耗 ${cost} 积分兑换 ${sku.activityCount.totalCount} 次抽奖机会？兑换成功后次数稍后到账。`,
      '确认兑换',
      { type: 'warning', confirmButtonText: '确认兑换', cancelButtonText: '取消' }
    )
  } catch {
    return // 取消
  }

  exchanging.value = true
  try {
    await apiCreditPayExchangeSku({ userId: user.userId, sku: sku.sku })
    // 本地扣减积分（金额按分计算避免浮点误差），不整页 reload
    user.setCredit((user.credit * 100 - toCents(Number(sku.productAmount))) / 100)
    ElMessage.success('兑换成功，抽奖次数稍后到账')
    // 库存为异步同步，不立即刷新验证；后台异步刷新一次积分兜底
    apiCredit(user.userId).then(user.setCredit).catch(() => {})
    // 刷新活动账户额度（次数到账后可见）
    raffle.refreshAccount(user.userId, user.activityId).catch(() => {})
  } catch (e: any) {
    ElMessage.error(e?.msg || '兑换失败')
  } finally {
    exchanging.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-card">
    <h2 class="page-title">积分兑换</h2>
    <div class="credit-bar">
      <span>我的积分：</span>
      <span class="credit-num">{{ user.credit.toFixed(2) }}</span>
      <span class="credit-tip">（兑换后抽奖次数稍后到账）</span>
    </div>
    <div v-loading="loading" class="sku-grid">
      <SkuCard
        v-for="sku in skuList"
        :key="sku.sku"
        :sku="sku"
        :disabled="exchanging"
        @exchange="onExchange"
      />
      <el-empty v-if="!loading && !skuList.length" description="活动暂无商品" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.credit-bar {
  margin-bottom: 16px;
  color: #606266;
  .credit-num {
    font-size: 20px;
    font-weight: 700;
    color: #e6a23c;
  }
  .credit-tip {
    color: #909399;
    font-size: 12px;
  }
}
.sku-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
</style>
