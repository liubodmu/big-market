<script setup lang="ts">
import type { ActivityDrawResponseDTO } from '@/types/api'

defineProps<{
  visible: boolean
  result: ActivityDrawResponseDTO | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()
</script>

<template>
  <el-dialog
    :model-value="visible"
    width="360px"
    :show-close="false"
    :close-on-click-modal="false"
    align-center
  >
    <template #header>
      <div class="dialog-title">抽奖结果</div>
    </template>
    <div class="award-body">
      <div class="award-name">{{ result?.awardTitle }}</div>
      <div class="award-tip">恭喜你抽中奖品！发奖处理中，稍后到账</div>
    </div>
    <template #footer>
      <el-button type="primary" @click="emit('close')">好的</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.dialog-title {
  font-size: 16px;
  font-weight: 600;
}
.award-body {
  text-align: center;
  padding: 12px 0;
  .award-name {
    font-size: 24px;
    font-weight: 700;
    color: #f56c6c;
    margin-bottom: 12px;
  }
  .award-tip {
    color: #909399;
    font-size: 13px;
  }
}
</style>
