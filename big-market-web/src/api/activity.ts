import { request } from '@/utils/request'
import { MOCK_ACCOUNT, MOCK_ACTIVITY_ID, MOCK_SIGNED, MOCK_SKU_LIST } from '@/mock/raffle'
import type {
  ActivityListResponseDTO,
  SkuProductResponseDTO,
  UserActivityAccountResponseDTO
} from '@/types/api'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

// 查询活动列表
export function apiActivityList(): Promise<ActivityListResponseDTO[]> {
  if (USE_MOCK) {
    return Promise.resolve([
      {
        activityId: MOCK_ACTIVITY_ID,
        activityName: '掘金狂潮 · 测试活动',
        activityDesc: '积分兑换抽奖次数，参与活动赢好礼',
        state: 'open'
      }
    ])
  }
  return request({ url: '/raffle/activity/query_activity_list', method: 'get' })
}

// 活动装配（运营）：GET，@RequestParam
export function apiArmory(activityId: number): Promise<boolean> {
  return request({ url: '/raffle/activity/armory', method: 'get', params: { activityId } })
}

// 查询 SKU 商品集合：无 @RequestBody，参数走 query string
export function apiSkuList(activityId: number): Promise<SkuProductResponseDTO[]> {
  if (USE_MOCK) return Promise.resolve(MOCK_SKU_LIST)
  return request({
    url: '/raffle/activity/query_sku_product_list_by_activity_id',
    method: 'post',
    params: { activityId }
  })
}

// 查询用户活动账户（与 raffle 里的一致，供 account 页使用）
export function apiActivityAccount(userId: string, activityId: number): Promise<UserActivityAccountResponseDTO> {
  if (USE_MOCK) return Promise.resolve(MOCK_ACCOUNT)
  return request({
    url: '/raffle/activity/query_user_activity_account',
    method: 'post',
    data: { userId, activityId }
  })
}

// 日历签到返利：@RequestParam，query string
export function apiCalendarSignRebate(userId: string): Promise<boolean> {
  if (USE_MOCK) {
    // mock：默认签到成功；可用 MOCK_SIGNED 控制重复签到场景
    if (MOCK_SIGNED) return Promise.reject({ code: '0003', msg: '操作已提交，请勿重复操作' })
    return Promise.resolve(true)
  }
  return request({
    url: '/raffle/activity/calendar_sign_rebate',
    method: 'post',
    params: { userId }
  })
}

// 查询是否已签到：@RequestParam，query string
export function apiIsCalendarSignRebate(userId: string): Promise<boolean> {
  if (USE_MOCK) return Promise.resolve(MOCK_SIGNED)
  return request({
    url: '/raffle/activity/is_calendar_sign_rebate',
    method: 'post',
    params: { userId }
  })
}
