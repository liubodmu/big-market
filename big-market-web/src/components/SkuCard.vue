<script setup lang="ts">
import { computed } from 'vue'
import type { SkuProductResponseDTO } from '@/types/api'
import { formatAmount } from '@/utils/format'

const props = defineProps<{
  sku: SkuProductResponseDTO
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'exchange', sku: SkuProductResponseDTO): void
}>()

const soldOut = computed(() => props.sku.stockCountSurplus <= 0)
// 商品名：X积分 换 Y次抽奖机会（activityCount.totalCount = 抽奖次数）
const productTitle = computed(
  () => `${formatAmount(props.sku.productAmount)}积分 换${props.sku.activityCount.totalCount}次抽奖机会`
)
</script>

<template>
  <div class="sku-card" :class="{ soldout: soldOut }">
    <div class="sku-name">{{ productTitle }}</div>
    <div class="sku-counts">
      <div>日 {{ sku.activityCount.dayCount }} 次 / 月 {{ sku.activityCount.monthCount }} 次</div>
      <div>剩余 {{ sku.stockCountSurplus }}</div>
    </div>
    <el-button
      class="sku-btn"
      type="danger"
      plain
      :disabled="soldOut || disabled"
      :loading="disabled"
      @click="emit('exchange', sku)"
    >
      {{ soldOut ? '已售罄' : disabled ? '兑换中' : '立即兑换' }}
    </el-button>
  </div>
</template>

<style scoped lang="scss">
.sku-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  &.soldout {
    opacity: 0.55;
  }
  .sku-name {
    font-size: 15px;
    font-weight: 600;
  }
  .sku-counts {
    color: #606266;
    font-size: 13px;
    line-height: 1.7;
    flex: 1;
  }
  .sku-btn {
    margin-top: auto;
  }
}
</style>
