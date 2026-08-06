import { defineStore } from 'pinia'
import { useUserStore } from '@/stores/user'
import {
  apiDraw,
  apiAwardList,
  apiAccount,
  apiRuleWeight
} from '@/api/raffle'
import { apiCredit } from '@/api/credit'
import type {
  ActivityDrawResponseDTO,
  RaffleAwardListResponseDTO,
  RaffleStrategyRuleWeightResponseDTO,
  UserActivityAccountResponseDTO
} from '@/types/api'

// 奖品池/权重配置低频变更，做 5 分钟本地缓存（按活动隔离）
const CACHE_TTL = 5 * 60 * 1000

interface CacheEntry<T> {
  at: number
  data: T
}

export const useRaffleStore = defineStore('raffle', {
  state: () => ({
    // 缓存按 activityId 隔离，避免切换活动后展示旧数据
    awardListCache: {} as Record<number, CacheEntry<RaffleAwardListResponseDTO[]>>,
    ruleWeightsCache: {} as Record<number, CacheEntry<RaffleStrategyRuleWeightResponseDTO[]>>,
    account: null as UserActivityAccountResponseDTO | null,
    // 抽奖锁：从点击到动画结束全程为 true，动画结束（onSpinEnd）才解锁
    drawing: false,
    lastResult: null as ActivityDrawResponseDTO | null,
    // 自增序列号，丢弃过期抽奖结果
    drawSeq: 0
  }),
  getters: {
    surplusToday: (s) => s.account?.dayCountSurplus ?? 0,
    surplusTotal: (s) => s.account?.totalCountSurplus ?? 0,
    // 当前活动奖品池：读取 user.activityId 对应缓存
    awardList: (s): RaffleAwardListResponseDTO[] => {
      const userStore = useUserStore()
      return s.awardListCache[userStore.activityId]?.data ?? []
    }
  },
  actions: {
    async refreshAwardList(userId: string, activityId: number, force = false) {
      const cached = this.awardListCache[activityId]
      if (!force && cached && Date.now() - cached.at < CACHE_TTL) {
        return cached.data
      }
      const data = await apiAwardList({ userId, activityId })
      this.awardListCache[activityId] = { at: Date.now(), data }
      return data
    },
    async refreshRuleWeights(userId: string, activityId: number, force = false) {
      const cached = this.ruleWeightsCache[activityId]
      if (!force && cached && Date.now() - cached.at < CACHE_TTL) {
        return cached.data
      }
      const data = await apiRuleWeight({ userId, activityId })
      this.ruleWeightsCache[activityId] = { at: Date.now(), data }
      return data
    },
    async refreshAccount(userId: string, activityId: number) {
      this.account = await apiAccount({ userId, activityId })
      return this.account
    },
    async refreshCredit(userId: string) {
      return apiCredit(userId)
    },
    // 切换活动时清空依赖旧活动的数据
    resetForActivity(activityId: number) {
      if (!this.awardListCache[activityId]) {
        // 保留当前活动缓存，清掉跨活动的临时状态
        this.lastResult = null
      }
      this.account = null
      this.drawing = false
    },
    // 发起抽奖请求；锁由 drawStart 置位、由动画结束 unlock 释放
    async draw(userId: string, activityId: number): Promise<ActivityDrawResponseDTO> {
      if (this.drawing) throw new Error('drawing')
      this.drawing = true
      const seq = ++this.drawSeq
      try {
        const res = await apiDraw({ userId, activityId })
        if (seq !== this.drawSeq) throw new Error('stale')
        this.lastResult = res
        // 抽奖成功后刷新剩余次数（不阻塞主流程）
        this.refreshAccount(userId, activityId).catch(() => {})
        return res
      } catch (e) {
        // 请求失败也要释放锁，否则页面卡死
        this.drawing = false
        throw e
      }
    },
    // 动画结束或超时兜底时调用，释放抽奖锁
    unlock() {
      this.drawing = false
    }
  }
})
