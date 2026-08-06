<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { apiUserAwardRecord } from '@/api/raffle'
import { awardStateText, isAwardComplete } from '@/utils/format'
import type { UserAwardRecordResponseDTO } from '@/types/api'

const user = useUserStore()
const records = ref<UserAwardRecordResponseDTO[]>([])
const loading = ref(false)
const loadError = ref(false)

function formatTime(t?: string): string {
  if (!t) return '-'
  const d = new Date(t)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function load() {
  loading.value = true
  loadError.value = false
  try {
    records.value = await apiUserAwardRecord(user.userId)
  } catch {
    loadError.value = true
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-card">
    <h2 class="page-title">中奖记录</h2>
    <el-alert
      v-if="loadError"
      type="error"
      title="中奖记录加载失败"
      :closable="false"
      show-icon
    >
      <template #default>
        <el-button size="small" type="primary" plain @click="load">重试</el-button>
      </template>
    </el-alert>
    <el-table v-loading="loading" :data="records" empty-text="暂无中奖记录" stripe>
      <el-table-column label="奖品" min-width="160">
        <template #default="{ row }">{{ row.awardTitle }}</template>
      </el-table-column>
      <el-table-column label="活动ID" prop="activityId" width="120" />
      <el-table-column label="中奖时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.awardTime) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="isAwardComplete(row.awardState) ? 'success' : 'warning'" size="small">
            {{ awardStateText(row.awardState) }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
