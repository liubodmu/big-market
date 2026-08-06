<script setup lang="ts">
import { ref } from 'vue'
import { apiUpdateDccConfig } from '@/api/dcc'

// 一期只做 rateLimiterSwitch；degradeSwitch 后端是死配置（@DCCValue 被注释），不做
const keyOptions = [
  { label: 'rateLimiterSwitch（限流开关）', value: 'rateLimiterSwitch' }
]
const valueOptions = [
  { label: 'open（开启）', value: 'open' },
  { label: 'close（关闭）', value: 'close' }
]

const key = ref('rateLimiterSwitch')
const value = ref('open')
const loading = ref(false)

async function update() {
  try {
    await ElMessageBox.confirm(
      `确认设置 ${key.value} = ${value.value}？该操作影响线上限流行为。`,
      'DCC 配置变更',
      { type: 'warning', confirmButtonText: '确认变更', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  loading.value = true
  try {
    await apiUpdateDccConfig({ key: key.value, value: value.value })
    ElMessage.success('配置已更新')
  } catch (e: any) {
    ElMessage.error(e?.msg || '配置更新失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-card">
    <h2 class="page-title">动态配置（运营）</h2>
    <el-alert
      type="warning"
      :closable="false"
      title="注意：当前值以 ZooKeeper 实际为准（后端暂无查询接口）。degradeSwitch 后端暂为死配置，一期不提供。"
      class="mb"
    />

    <el-form label-width="120px" class="form">
      <el-form-item label="配置项">
        <el-select v-model="key" style="width: 260px">
          <el-option v-for="o in keyOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="值">
        <el-radio-group v-model="value">
          <el-radio v-for="o in valueOptions" :key="o.value" :value="o.value">
            {{ o.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="update">更新配置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped lang="scss">
.mb {
  margin-bottom: 16px;
}
.form {
  max-width: 520px;
}
</style>
