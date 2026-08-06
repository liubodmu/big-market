<script setup lang="ts">
import { computed, ref } from 'vue'
import type { RaffleAwardListResponseDTO } from '@/types/api'

// 扇区从 12 点方向（-90°）开始顺时针排布
const SECTOR_START = -90
const CX = 160
const CY = 160
const R = 150

const colors = ['#ffd9d9', '#ffe7c2', '#d9e8ff', '#d9f5e3', '#f0d9ff', '#fff3c2']

const props = defineProps<{
  awardList: RaffleAwardListResponseDTO[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'spinEnd'): void
  (e: 'centerClick'): void
}>()

const currentDeg = ref(0)
const spinDeg = ref(0)
const transitioning = ref(false)

const sectorAngle = computed(() =>
  props.awardList.length ? 360 / props.awardList.length : 0
)

// 单个扇区 path：扇形 = 中心点 + 两条半径 + 圆弧
function sectorPath(i: number): string {
  const a0 = (SECTOR_START + i * sectorAngle.value) * (Math.PI / 180)
  const a1 = (SECTOR_START + (i + 1) * sectorAngle.value) * (Math.PI / 180)
  const x0 = CX + R * Math.cos(a0)
  const y0 = CY + R * Math.sin(a0)
  const x1 = CX + R * Math.cos(a1)
  const y1 = CY + R * Math.sin(a1)
  const largeArc = sectorAngle.value > 180 ? 1 : 0
  return `M${CX},${CY} L${x0.toFixed(2)},${y0.toFixed(2)} A${R},${R} 0 ${largeArc} 1 ${x1.toFixed(2)},${y1.toFixed(2)} Z`
}

// 标签位置：扇区角平分线中点
function labelXY(i: number): { x: number; y: number } {
  const mid = (SECTOR_START + (i + 0.5) * sectorAngle.value) * (Math.PI / 180)
  return {
    x: CX + (R * 0.72) * Math.cos(mid),
    y: CY + (R * 0.72) * Math.sin(mid)
  }
}

// 文字沿半径方向排布；当扇区中心在下半盘时翻转 180° 保持正向可读
function labelTransform(i: number): string {
  const { x, y } = labelXY(i)
  const center = SECTOR_START + (i + 0.5) * sectorAngle.value
  const deg = center % 360
  // -90~90 区间文字沿半径向外；其余翻转 180 保持正向
  const rot = deg >= -90 && deg <= 90 ? deg : deg + 180
  return `rotate(${rot} ${x} ${y})`
}

// 命中索引 = 用 awardId 在奖品池找下标
function targetIndex(awardId: number): number {
  const idx = props.awardList.findIndex((a) => a.awardId === awardId)
  return idx >= 0 ? idx : 0
}

// 相对增量公式：让命中扇区中心对准顶部指针（-90°）
function spinTo(awardId: number) {
  if (!props.awardList.length || transitioning.value) return
  const idx = targetIndex(awardId)
  // 扇区 idx 中心在角度 center = SECTOR_START + (idx+0.5)*sectorAngle
  // 转盘旋转 θ 后中心变为 center+θ，要停在顶部指针（-90°）：center+θ ≡ -90 → θ ≡ -90-center
  const center = SECTOR_START + (idx + 0.5) * sectorAngle.value
  const finalAngle = ((-90 - center) % 360 + 360) % 360
  const delta = ((finalAngle - (currentDeg.value % 360)) + 360) % 360
  spinDeg.value = currentDeg.value + 4 * 360 + delta
  transitioning.value = true
}

function onTransitionEnd() {
  if (!transitioning.value) return
  transitioning.value = false
  currentDeg.value = spinDeg.value % 360
  emit('spinEnd')
}

function onCenterClick() {
  emit('centerClick')
}

defineExpose({ spinTo })
</script>

<template>
  <div class="wheel-wrap">
    <div class="wheel-box">
      <svg
        viewBox="0 0 320 320"
        class="wheel-svg"
        :class="{ transitioning }"
        :style="{ transform: `rotate(${spinDeg}deg)` }"
        @transitionend="onTransitionEnd"
      >
        <g>
          <template v-for="(award, i) in awardList" :key="award.awardId">
            <path
              :d="sectorPath(i)"
              :fill="colors[i % colors.length]"
              stroke="#fff"
              stroke-width="1.5"
            />
            <text
              :x="labelXY(i).x"
              :y="labelXY(i).y"
              :transform="labelTransform(i)"
              text-anchor="middle"
              dominant-baseline="middle"
              font-size="12"
              font-weight="500"
              fill="#333"
            >
              {{ award.awardTitle }}
            </text>
          </template>
        </g>
      </svg>
      <!-- 中心按钮（固定不随转盘旋转） -->
      <div
        class="wheel-center"
        :class="{ disabled: disabled }"
        @click="!disabled && onCenterClick()"
      >
        {{ disabled ? '抽奖中' : '抽奖' }}
      </div>
      <!-- 顶部指针（固定） -->
      <div class="wheel-pointer"></div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.wheel-wrap {
  display: flex;
  justify-content: center;
}
.wheel-box {
  position: relative;
  width: 420px;
  height: 420px;
}
.wheel-svg {
  width: 100%;
  height: 100%;
  display: block;
  transform-origin: 50% 50%;
}
.wheel-svg.transitioning {
  transition: transform 1.2s cubic-bezier(0.2, 0.7, 0.3, 1);
}
.wheel-center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 76px;
  height: 76px;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: #f56c6c;
  cursor: pointer;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  user-select: none;
  &.disabled {
    color: #c0c4cc;
    cursor: not-allowed;
  }
  &:hover:not(.disabled) {
    color: #d0453c;
  }
}
.wheel-pointer {
  position: absolute;
  left: 50%;
  top: -4px;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 13px solid transparent;
  border-right: 13px solid transparent;
  border-top: 22px solid #f56c6c;
  z-index: 3;
}
</style>
