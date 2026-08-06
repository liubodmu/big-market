import { request } from '@/utils/request'
import {
  MOCK_ACCOUNT,
  MOCK_AWARD_LIST,
  MOCK_RULE_WEIGHTS,
  mockDraw
} from '@/mock/raffle'
import type {
  ActivityDrawRequestDTO,
  ActivityDrawResponseDTO,
  RaffleAwardListRequestDTO,
  RaffleAwardListResponseDTO,
  RaffleStrategyRuleWeightRequestDTO,
  RaffleStrategyRuleWeightResponseDTO,
  UserActivityAccountRequestDTO,
  UserActivityAccountResponseDTO,
  UserAwardRecordResponseDTO
} from '@/types/api'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

// draw：注意该接口 @RequestBody，body JSON
export function apiDraw(data: ActivityDrawRequestDTO): Promise<ActivityDrawResponseDTO> {
  if (USE_MOCK) return Promise.resolve(mockDraw())
  return request({ url: '/raffle/activity/draw', method: 'post', data })
}

// 抽奖奖品列表
export function apiAwardList(
  data: RaffleAwardListRequestDTO
): Promise<RaffleAwardListResponseDTO[]> {
  if (USE_MOCK) return Promise.resolve(MOCK_AWARD_LIST)
  return request({ url: '/raffle/strategy/query_raffle_award_list', method: 'post', data })
}

// 权重档位
export function apiRuleWeight(
  data: RaffleStrategyRuleWeightRequestDTO
): Promise<RaffleStrategyRuleWeightResponseDTO[]> {
  if (USE_MOCK) return Promise.resolve(MOCK_RULE_WEIGHTS)
  return request({
    url: '/raffle/strategy/query_raffle_strategy_rule_weight',
    method: 'post',
    data
  })
}

// 用户活动账户
export function apiAccount(
  data: UserActivityAccountRequestDTO
): Promise<UserActivityAccountResponseDTO> {
  if (USE_MOCK) return Promise.resolve(MOCK_ACCOUNT)
  return request({ url: '/raffle/activity/query_user_activity_account', method: 'post', data })
}

// 用户中奖记录：@RequestParam，query string
export function apiUserAwardRecord(userId: string): Promise<UserAwardRecordResponseDTO[]> {
  if (USE_MOCK) {
    return Promise.resolve([
      {
        userId,
        activityId: 100301,
        strategyId: 100006,
        orderId: '888888888888',
        awardId: 102,
        awardTitle: 'OpenAI会员卡',
        awardTime: new Date().toISOString(),
        awardState: 'complete'
      },
      {
        userId,
        activityId: 100301,
        strategyId: 100006,
        orderId: '777777777777',
        awardId: 101,
        awardTitle: '随机积分',
        awardTime: new Date(Date.now() - 86400000).toISOString(),
        awardState: 'create'
      }
    ])
  }
  return request({
    url: '/raffle/activity/query_user_award_record',
    method: 'post',
    params: { userId }
  })
}
