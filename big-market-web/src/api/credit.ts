import { request } from '@/utils/request'
import { MOCK_CREDIT } from '@/mock/raffle'
import type { SkuProductShopCartRequestDTO } from '@/types/api'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

// 查询用户积分：无 @RequestBody，参数走 query string
export function apiCredit(userId: string): Promise<number> {
  if (USE_MOCK) return Promise.resolve(MOCK_CREDIT)
  return request({
    url: '/raffle/activity/query_user_credit_account',
    method: 'post',
    params: { userId }
  })
}

// 积分兑换商品：@RequestBody
export function apiCreditPayExchangeSku(data: SkuProductShopCartRequestDTO): Promise<boolean> {
  if (USE_MOCK) {
    return Promise.resolve(true)
  }
  return request({ url: '/raffle/activity/credit_pay_exchange_sku', method: 'post', data })
}
