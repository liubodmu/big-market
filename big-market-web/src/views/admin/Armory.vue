<script setup lang="ts">
import { ref } from 'vue'
import { apiArmory } from '@/api/activity'
import { apiStrategyArmory } from '@/api/dcc'

const activityId = ref<number | null>(100301)
const strategyId = ref<number | null>(100001)
const loading = ref(false)

async function doArmory() {
  if (!activityId.value) {
    ElMessage.warning('请输入活动ID')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认装配活动 ${activityId.value}？装配后生效。`,
      '活动装配',
      { type: 'warning', confirmButtonText: '确认装配', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  loading.value = true
  try {
    await apiArmory(activityId.value)
    ElMessage.success(`活动 ${activityId.value} 装配成功`)
  } catch (e: any) {
    ElMessage.error(e?.msg || '装配失败')
  } finally {
    loading.value = false
  }
}

async function doStrategyArmory() {
  if (!strategyId.value) {
    ElMessage.warning('请输入策略ID')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认装配策略 ${strategyId.value}？装配后生效。`,
      '策略装配',
      { type: 'warning', confirmButtonText: '确认装配', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  loading.value = true
  try {
    await apiStrategyArmory(strategyId.value)
    ElMessage.success(`策略 ${strategyId.value} 装配成功`)
  } catch (e: any) {
    ElMessage.error(e?.msg || '装配失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-card">
    <h2 class="page-title">数据装配（运营）</h2>
    <el-alert
      type="info"
      :closable="false"
      title="提示：装配是写 Redis 的操作，装配后生效；改库表后需重新装配或手动清理缓存。"
      class="mb"
    />

    <el-form label-width="90px" class="form">
      <el-form-item label="活动装配">
        <el-input-number v-model="activityId" :min="1" style="width: 200px" />
        <el-button class="ml" type="primary" :loading="loading" @click="doArmory">
          装配活动
        </el-button>
      </el-form-item>
      <el-form-item label="策略装配">
        <el-input-number v-model="strategyId" :min="1" style="width: 200px" />
        <el-button class="ml" type="primary" :loading="loading" @click="doStrategyArmory">
          装配策略
        </el-button>
      </el-form-item>
    </el-form>

    <el-divider />
    <div class="hint">
      种子数据参考：活动 100301 -> 策略 100006；策略 100001 有完整奖品配置。
    </div>
  </div>
</template>

<style scoped lang="scss">
.mb {
  margin-bottom: 16px;
}
.form {
  max-width: 480px;
}
.ml {
  margin-left: 12px;
}
.hint {
  color: #909399;
  font-size: 13px;
}
</style>
