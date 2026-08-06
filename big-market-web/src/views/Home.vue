<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { apiActivityList } from '@/api/activity'
import { apiCredit } from '@/api/credit'
import type { ActivityListResponseDTO } from '@/types/api'

const user = useUserStore()
const router = useRouter()

const activityList = ref<ActivityListResponseDTO[]>([])
const loading = ref(false)

async function loadActivityList() {
  loading.value = true
  try {
    activityList.value = await apiActivityList()
    // 默认选中第一个活动
    if (activityList.value.length && !user.activityId) {
      user.setActivity(activityList.value[0].activityId)
    }
  } finally {
    loading.value = false
  }
}

async function loadCredit() {
  try {
    user.setCredit(await apiCredit(user.userId))
  } catch {
    // 忽略
  }
}

function goActivity(activity: ActivityListResponseDTO) {
  user.setActivity(activity.activityId)
  router.push('/raffle')
}

onMounted(async () => {
  await Promise.all([loadActivityList(), loadCredit()])
})
</script>

<template>
  <div>
    <el-card shadow="never" class="mb">
      <div class="hero">
        <div>
          <h2 class="hero-title">掘金狂潮</h2>
          <p class="hero-desc">积分兑换抽奖次数，参与活动赢好礼</p>
        </div>
      </div>
    </el-card>

    <div class="page-card">
      <h3 class="sec-title">当前活动</h3>
      <div v-loading="loading" class="activity-grid">
        <div
          v-for="activity in activityList"
          :key="activity.activityId"
          class="activity-card"
          @click="goActivity(activity)"
        >
          <div class="activity-name">{{ activity.activityName }}</div>
          <div class="activity-desc">{{ activity.activityDesc || '参与抽奖赢好礼' }}</div>
          <div class="activity-foot">
            <el-tag size="small" :type="activity.state === 'open' ? 'success' : 'info'">
              {{ activity.state === 'open' ? '进行中' : '已结束' }}
            </el-tag>
            <span class="activity-go">去抽奖 →</span>
          </div>
        </div>
        <el-empty v-if="!loading && !activityList.length" description="暂无进行中的活动" />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.mb {
  margin-bottom: 16px;
}
.hero {
  .hero-title {
    margin: 0 0 8px;
  }
  .hero-desc {
    margin: 0;
    color: #909399;
  }
}
.sec-title {
  margin: 0 0 16px;
}
.activity-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.activity-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  .activity-name {
    font-size: 17px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  .activity-desc {
    color: #909399;
    font-size: 13px;
    min-height: 20px;
    margin-bottom: 14px;
  }
  .activity-foot {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .activity-go {
      color: #f56c6c;
      font-size: 13px;
    }
  }
}
</style>
