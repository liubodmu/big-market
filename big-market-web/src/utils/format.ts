// 金额展示：BigDecimal 序列化为 JSON number，展示用 toFixed(2)
export function formatAmount(v?: number | null): string {
  if (v === null || v === undefined) return '0.00'
  return Number(v).toFixed(2)
}

// 金额计算：禁止浮点乘除，统一转整数分后再算
export function toCents(v: number): number {
  return Math.round(v * 100)
}

export function fromCents(cents: number): number {
  return cents / 100
}

// 中奖记录状态：后端 AwardStateVO code 为 create/complete/fail
// （历史脏数据曾为 completed，统一在此归一化，避免 create/completed/complete 混用）
export type AwardStateCode = 'create' | 'complete' | 'fail'

export function isAwardComplete(state?: string): boolean {
  return state === 'complete' || state === 'completed'
}

export function awardStateText(state?: string): string {
  if (isAwardComplete(state)) return '已发奖'
  if (state === 'fail') return '发奖失败'
  return '发奖中'
}
