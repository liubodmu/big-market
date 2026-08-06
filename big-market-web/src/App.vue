<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { apiCredit } from '@/api/credit'
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const user = useUserStore()
const router = useRouter()

async function loadCredit() {
  try {
    user.setCredit(await apiCredit(user.userId))
  } catch {
    // 忽略：右上角积分拉取失败不影响主流程
  }
}

onMounted(loadCredit)
</script>

<template>
  <el-config-provider :locale="zhCn">
    <div class="app-shell">
      <header class="app-header">
        <div class="logo" @click="router.push('/')">掘金狂潮</div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/raffle">抽奖</router-link>
          <router-link to="/account">我的账户</router-link>
          <router-link to="/exchange">积分兑换</router-link>
          <router-link to="/records">中奖记录</router-link>
        </nav>
        <div class="header-right">
          <span class="credit">积分：{{ user.credit.toFixed(2) }}</span>
          <el-tag type="info" effect="plain">演示账号</el-tag>
        </div>
      </header>
      <main class="app-main">
        <router-view />
      </main>
    </div>
  </el-config-provider>
</template>

<style scoped lang="scss">
.app-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 24px;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #eee;
  .logo {
    font-size: 18px;
    font-weight: 600;
    color: #f56c6c;
    cursor: pointer;
  }
  .nav {
    flex: 1;
    display: flex;
    gap: 16px;
    a {
      color: #333;
      text-decoration: none;
      &.router-link-active {
        color: #f56c6c;
        font-weight: 600;
      }
    }
  }
  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;
    .credit {
      color: #e6a23c;
      font-weight: 600;
    }
  }
}
.app-main {
  max-width: 1080px;
  margin: 24px auto;
  padding: 0 16px;
}
</style>
