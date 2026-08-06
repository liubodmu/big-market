import { request } from '@/utils/request'
import type { DccUpdateRequest } from '@/types/api'

// 动态配置更新：GET，@RequestParam key/value
// 注意：一期只做 rateLimiterSwitch；degradeSwitch 后端是死配置（需恢复注解才生效）
export function apiUpdateDccConfig(data: DccUpdateRequest): Promise<boolean> {
  return request({
    url: '/raffle/dcc/update_config',
    method: 'get',
    params: data
  })
}

// 策略装配（运营）：GET，@RequestParam
export function apiStrategyArmory(strategyId: number): Promise<boolean> {
  return request({
    url: '/raffle/strategy/strategy_armory',
    method: 'get',
    params: { strategyId }
  })
}
