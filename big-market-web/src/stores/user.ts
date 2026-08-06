import { defineStore } from 'pinia'

// 演示项目：固定演示账号，隐藏真实 userId（右上角只展示"演示账号"）
const DEMO_USER_ID = 'xiaofuge'
const DEFAULT_ACTIVITY_ID = 100301

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: DEMO_USER_ID,
    activityId: Number(localStorage.getItem('bm_activityId') || DEFAULT_ACTIVITY_ID) as number,
    credit: 0 as number
  }),
  actions: {
    setUser() {
      // 演示阶段固定账号，忽略外部传入
      this.userId = DEMO_USER_ID
    },
    setActivity(id: number) {
      this.activityId = id
      localStorage.setItem('bm_activityId', String(id))
    },
    setCredit(v: number) {
      this.credit = v
    }
  }
})
