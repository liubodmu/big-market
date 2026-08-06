<script setup lang="ts">
import { onMounted, ref } from 'vue'
import Wheel from '@/components/Wheel.vue'
import AwardDialog from '@/components/AwardDialog.vue'
import { useUserStore } from '@/stores/user'
import { useRaffleStore } from '@/stores/raffle'
import { mockDrawError } from '@/mock/raffle'
import type { ActivityDrawResponseDTO } from '@/types/api'

const user = useUserStore()
const raffle = useRaffleStore()

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'
// mock 模式下的异常演示入口（仅 VITE_USE_MOCK=true 显示，生产构建不会带出）
const mockErrorOptions = [
  { label: '无（正常抽奖）', value: '' },
  { label: '活动降级 0004', value: '0004' },
  { label: '限流 0005', value: '0005' },
  { label: '熔断 0006', value: '0006' },
  { label: '日次数不足 ERR_BIZ_008', value: 'ERR_BIZ_008' }
]
const mockError = ref('')

const loading = ref(false)
const dialogVisible = ref(false)
const result = ref<ActivityDrawResponseDTO | null>(null)
const wheelRef = ref<InstanceType<typeof Wheel> | null>(null)
// transitionend 兜底定时器：动画超时强制结束，避免锁死
let spinFallbackTimer: ReturnType<typeof setTimeout> | null = null

async function init() {
  loading.value = true
  try {
    await Promise.all([
      raffle.refreshAwardList(user.userId, user.activityId),
      raffle.refreshAccount(user.userId, user.activityId)
    ])
  } finally {
    loading.value = false
  }
}
onMounted(init)

async function handleDraw() {
  // 锁在请求期间和动画期间都为 true，动画结束才解锁
  if (raffle.drawing) return
  // 前置快速拦截：剩余次数不足
  if (raffle.surplusToday <= 0) {
    ElMessage.warning('今日抽奖次数已用完')
    return
  }
  try {
    // mock 模式：若选择了模拟异常，直接触发对应错误分支
    if (USE_MOCK && mockError.value) {
      await mockDrawError(mockError.value as '0004' | '0005' | '0006' | 'ERR_BIZ_008')
      return
    }
    // draw 内部会置 drawing=true；请求返回后开始转盘动画（锁保持）
    const res = await raffle.draw(user.userId, user.activityId)
    result.value = res
    wheelRef.value?.spinTo(res.awardId)
    // 兜底：1.5s 动画 + 缓冲，若 transitionend 未触发则强制结束
    if (spinFallbackTimer) clearTimeout(spinFallbackTimer)
    spinFallbackTimer = setTimeout(finishSpin, 2500)
  } catch (e: any) {
    if (e?.code !== 'drawing' && e?.message !== 'stale') {
      ElMessage.error(e?.msg || '抽奖失败，请稍后再试')
    }
  }
}

function finishSpin() {
  if (spinFallbackTimer) {
    clearTimeout(spinFallbackTimer)
    spinFallbackTimer = null
  }
  // 释放抽奖锁（正常路径由 onSpinEnd 触发，这里为兜底）
  raffle.unlock()
  if (result.value) dialogVisible.value = true
}

function onSpinEnd() {
  finishSpin()
}
</script>

<template>
  <div class="page-card raffle-page">
    <h2 class="page-title">抽奖</h2>
    <div v-if="USE_MOCK" class="mock-bar">
      <span class="mock-label">Mock 模拟异常：</span>
      <el-select v-model="mockError" size="small" style="width: 200px" clearable placeholder="选择要模拟的分支">
        <el-option
          v-for="opt in mockErrorOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </div>
    <div v-loading="loading" class="raffle-content">
      <Wheel
        ref="wheelRef"
        :award-list="raffle.awardList"
        :disabled="raffle.drawing"
        @spin-end="onSpinEnd"
        @center-click="handleDraw"
      />

      <div class="award-pool">
        <div class="pool-title">奖品池</div>
        <div class="pool-grid">
          <div v-for="award in raffle.awardList" :key="award.awardId" class="pool-item">
            <div class="pool-name">{{ award.awardTitle }}</div>
          </div>
        </div>
      </div>
    </div>

    <AwardDialog :visible="dialogVisible" :result="result" @close="dialogVisible = false" />
  </div>
</template>

<style scoped lang="scss">
.mock-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  margin-bottom: 16px;
  .mock-label {
    font-size: 13px;
    color: #909399;
  }
}
.raffle-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}
.award-pool {
  width: 100%;
  max-width: 460px;
  .pool-title {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 12px;
    text-align: center;
  }
  .pool-grid {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
  }
  .pool-item {
    border: 1px solid #eee;
    border-radius: 6px;
    padding: 8px 14px;
    background: #fafafa;
    .pool-name {
      font-size: 13px;
      font-weight: 500;
    }
  }
}
</style>
