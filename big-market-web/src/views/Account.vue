<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRaffleStore } from '@/stores/raffle'
import {
  apiCalendarSignRebate,
  apiIsCalendarSignRebate
} from '@/api/activity'

const user = useUserStore()
const raffle = useRaffleStore()

const signed = ref(false)
const signing = ref(false)
const loading = ref(false)

const todayText = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
})

async function init() {
  loading.value = true
  try {
    await Promise.all([
      raffle.refreshAccount(user.userId, user.activityId),
      apiIsCalendarSignRebate(user.userId).then((v) => (signed.value = v))
    ])
  } finally {
    loading.value = false
  }
}

async function sign() {
  if (signing.value || signed.value) return
  signing.value = true
  try {
    await apiCalendarSignRebate(user.userId)
    signed.value = true
    ElMessage.success('签到成功')
    await raffle.refreshAccount(user.userId, user.activityId)
  } catch (e: any) {
    if (e?.code === '0003') {
      signed.value = true
      ElMessage.warning('今日已签到')
    } else {
      ElMessage.error(e?.msg || '签到失败')
    }
  } finally {
    signing.value = false
  }
}

onMounted(init)
</script>

<template>
  <div class="page-card">
    <h2 class="page-title">我的账户</h2>

    <div v-loading="loading" class="account-grid">
      <div class="acc-item">
        <div class="acc-label">总次数</div>
        <div class="acc-value">{{ raffle.account?.totalCount ?? 0 }}</div>
        <div class="acc-sub">剩余 {{ raffle.account?.totalCountSurplus ?? 0 }}</div>
      </div>
      <div class="acc-item">
        <div class="acc-label">月次数</div>
        <div class="acc-value">{{ raffle.account?.monthCount ?? 0 }}</div>
        <div class="acc-sub">剩余 {{ raffle.account?.monthCountSurplus ?? 0 }}</div>
      </div>
      <div class="acc-item">
        <div class="acc-label">日次数</div>
        <div class="acc-value">{{ raffle.account?.dayCount ?? 0 }}</div>
        <div class="acc-sub">剩余 {{ raffle.account?.dayCountSurplus ?? 0 }}</div>
      </div>
    </div>

    <el-divider />

    <h3 class="sec-title">日历签到</h3>
    <div class="sign-row">
      <div class="sign-info">
        <div class="sign-date">{{ todayText }}</div>
        <div class="sign-tip">
          {{ signed ? '今日已签到，明天再来' : '每日签到可领取返利，用于补充抽奖次数' }}
        </div>
      </div>
      <el-button
        type="danger"
        :loading="signing"
        :disabled="signed"
        @click="sign"
      >
        {{ signed ? '今日已签到' : '立即签到' }}
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.account-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  .acc-item {
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 16px;
    text-align: center;
    .acc-label {
      color: #909399;
      font-size: 13px;
    }
    .acc-value {
      font-size: 26px;
      font-weight: 700;
      color: #f56c6c;
      margin: 8px 0;
    }
    .acc-sub {
      color: #909399;
      font-size: 12px;
    }
  }
}
.sec-title {
  margin: 0 0 16px;
}
.sign-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px dashed #eee;
  border-radius: 8px;
  padding: 16px;
  .sign-date {
    font-size: 16px;
    font-weight: 600;
  }
  .sign-tip {
    color: #909399;
    font-size: 13px;
    margin-top: 4px;
  }
}
</style>
