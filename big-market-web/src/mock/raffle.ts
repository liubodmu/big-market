// Mock 数据：统一使用 SQL 种子真实数据
// userId=xiaofuge、activityId=100301、SKU=9011-9014、
// 奖品 101 随机积分 / 102 5次使用 / 103 10次使用 / 104 20次使用 / 105 增加gpt-4对话模型 / 106 增加dall-e-2画图模型
import type {
  ActivityDrawResponseDTO,
  RaffleAwardListResponseDTO,
  RaffleStrategyRuleWeightResponseDTO,
  SkuProductResponseDTO,
  UserActivityAccountResponseDTO
} from '@/types/api'

export const MOCK_ACTIVITY_ID = 100301
export const MOCK_SKU_LIST: SkuProductResponseDTO[] = [
  {
    sku: 9011,
    activityId: 100301,
    activityCountId: 11101,
    stockCount: 100000,
    stockCountSurplus: 99887,
    productAmount: 200.0,
    activityCount: { totalCount: 100, dayCount: 10, monthCount: 30 }
  },
  {
    sku: 9012,
    activityId: 100301,
    activityCountId: 11102,
    stockCount: 100000,
    stockCountSurplus: 99885,
    productAmount: 100.0,
    activityCount: { totalCount: 100, dayCount: 10, monthCount: 30 }
  },
  {
    sku: 9013,
    activityId: 100301,
    activityCountId: 11103,
    stockCount: 100000,
    stockCountSurplus: 99884,
    productAmount: 20.0,
    activityCount: { totalCount: 100, dayCount: 10, monthCount: 30 }
  },
  {
    sku: 9014,
    activityId: 100301,
    activityCountId: 11104,
    stockCount: 100000,
    stockCountSurplus: 99866,
    productAmount: 5.0,
    activityCount: { totalCount: 100, dayCount: 10, monthCount: 30 }
  }
]

export const MOCK_ACCOUNT: UserActivityAccountResponseDTO = {
  totalCount: 100,
  totalCountSurplus: 97,
  dayCount: 10,
  dayCountSurplus: 3,
  monthCount: 30,
  monthCountSurplus: 18
}

// 奖品池：3 个未解锁奖品用于演示"再抽N次解锁"
export const MOCK_AWARD_LIST: RaffleAwardListResponseDTO[] = [
  { awardId: 101, awardTitle: '随机积分', sort: 1, isAwardUnlock: true, waitUnLockCount: 0 },
  { awardId: 102, awardTitle: '5 次使用', sort: 2, isAwardUnlock: true, waitUnLockCount: 0 },
  { awardId: 103, awardTitle: '10 次使用', sort: 3, awardSubtitle: '抽奖3次后解锁', awardRuleLockCount: 3, isAwardUnlock: false, waitUnLockCount: 2 },
  { awardId: 104, awardTitle: '20 次使用', sort: 4, awardSubtitle: '抽奖5次后解锁', awardRuleLockCount: 5, isAwardUnlock: false, waitUnLockCount: 4 },
  { awardId: 105, awardTitle: '增加gpt-4对话模型', sort: 5, isAwardUnlock: true, waitUnLockCount: 0 },
  { awardId: 106, awardTitle: '增加dall-e-2画图模型', sort: 6, isAwardUnlock: true, waitUnLockCount: 0 }
]

export const MOCK_RULE_WEIGHTS: RaffleStrategyRuleWeightResponseDTO[] = [
  {
    ruleWeightCount: 4000,
    userActivityAccountTotalUseCount: 20,
    strategyAwards: [
      { awardId: 102, awardTitle: '5 次使用' },
      { awardId: 103, awardTitle: '10 次使用' },
      { awardId: 104, awardTitle: '20 次使用' }
    ]
  },
  {
    ruleWeightCount: 5000,
    userActivityAccountTotalUseCount: 20,
    strategyAwards: [
      { awardId: 102, awardTitle: '5 次使用' },
      { awardId: 103, awardTitle: '10 次使用' },
      { awardId: 104, awardTitle: '20 次使用' },
      { awardId: 105, awardTitle: '增加gpt-4对话模型' }
    ]
  },
  {
    ruleWeightCount: 6000,
    userActivityAccountTotalUseCount: 20,
    strategyAwards: [
      { awardId: 102, awardTitle: '5 次使用' },
      { awardId: 103, awardTitle: '10 次使用' },
      { awardId: 104, awardTitle: '20 次使用' },
      { awardId: 105, awardTitle: '增加gpt-4对话模型' },
      { awardId: 106, awardTitle: '增加dall-e-2画图模型' }
    ]
  }
]

export const MOCK_CREDIT = 100.0

// 模拟签到状态：true 表示今日已签到（用于演示重复签到分支）
export const MOCK_SIGNED = false

// draw 返回结果；可传 awardId 控制命中（用于转盘联调）
export function mockDraw(awardId?: number): ActivityDrawResponseDTO {
  const id = awardId ?? 102
  const award = MOCK_AWARD_LIST.find((a) => a.awardId === id) ?? MOCK_AWARD_LIST[0]
  return { awardId: award.awardId, awardTitle: award.awardTitle, awardIndex: award.sort }
}

// 模拟降级/限流/熔断分支：mock 模式下可通过该入口构造错误
export function mockDrawError(code: '0004' | '0005' | '0006' | 'ERR_BIZ_008'): Promise<never> {
  const msgMap: Record<string, string> = {
    '0004': '活动已降级，请稍后再试',
    '0005': '操作太频繁，请稍后再试',
    '0006': '系统繁忙，请稍后再试',
    ERR_BIZ_008: '今日抽奖次数已用完'
  }
  return Promise.reject({ code, msg: msgMap[code] })
}
