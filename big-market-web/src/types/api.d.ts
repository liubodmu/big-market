// 后端统一响应包装 Response<T> { code, info, data }
export interface ApiResponse<T> {
  code: string
  info: string
  data: T
}

// 活动抽奖请求/响应
export interface ActivityDrawRequestDTO {
  userId: string
  activityId: number
}
export interface ActivityDrawResponseDTO {
  awardId: number
  awardTitle: string
  awardIndex: number
}

// 活动列表
export interface ActivityListResponseDTO {
  activityId: number
  activityName: string
  activityDesc?: string
  beginDateTime?: string
  endDateTime?: string
  strategyId?: number
  state?: string
}

// 中奖记录
export interface UserAwardRecordResponseDTO {
  userId: string
  activityId: number
  strategyId: number
  orderId: string
  awardId: number
  awardTitle: string
  awardTime?: string
  awardState: string
}

// 抽奖奖品列表
export interface RaffleAwardListRequestDTO {
  userId: string
  activityId: number
}
export interface RaffleAwardListResponseDTO {
  awardId: number
  awardTitle: string
  awardSubtitle?: string
  sort: number
  awardRuleLockCount?: number
  isAwardUnlock: boolean
  waitUnLockCount: number
}

// 权重档位
export interface RaffleStrategyRuleWeightRequestDTO {
  userId: string
  activityId: number
}
export interface RaffleStrategyRuleWeightResponseDTO {
  ruleWeightCount: number
  userActivityAccountTotalUseCount: number
  strategyAwards: StrategyAward[]
}
export interface StrategyAward {
  awardId: number
  awardTitle: string
}

// 用户活动账户
export interface UserActivityAccountRequestDTO {
  userId: string
  activityId: number
}
export interface UserActivityAccountResponseDTO {
  totalCount: number
  totalCountSurplus: number
  dayCount: number
  dayCountSurplus: number
  monthCount: number
  monthCountSurplus: number
}

// SKU 商品
export interface SkuProductResponseDTO {
  sku: number
  activityId: number
  activityCountId: number
  stockCount: number
  stockCountSurplus: number
  productAmount: number
  activityCount: {
    totalCount: number
    dayCount: number
    monthCount: number
  }
}

// 积分兑换请求
export interface SkuProductShopCartRequestDTO {
  userId: string
  sku: number
}

// 随机抽奖（运营/测试）
export interface RaffleStrategyRequestDTO {
  strategyId: number
}
export interface RaffleStrategyResponseDTO {
  awardId: number
  awardIndex: number
}

// DCC 动态配置
export interface DccUpdateRequest {
  key: string
  value: string
}

// 统一错误对象（拦截器 reject 时携带）
export interface BizError {
  code: string
  msg: string
}
