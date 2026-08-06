import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
    { path: '/raffle', name: 'Raffle', component: () => import('@/views/Raffle.vue') },
    { path: '/account', name: 'Account', component: () => import('@/views/Account.vue') },
    { path: '/exchange', name: 'Exchange', component: () => import('@/views/Exchange.vue') },
    { path: '/records', name: 'Records', component: () => import('@/views/Records.vue') },
    { path: '/admin/armory', name: 'AdminArmory', component: () => import('@/views/admin/Armory.vue') },
    { path: '/admin/dcc', name: 'AdminDcc', component: () => import('@/views/admin/DccConfig.vue') }
  ]
})

// 守卫：需要 userId 的页面，未填则引导到首页填写（不硬阻断）
router.beforeEach((to) => {
  const needUser = !['Home', 'AdminArmory', 'AdminDcc'].includes(to.name as string)
  if (needUser) {
    const user = useUserStore()
    if (!user.userId) return { name: 'Home' }
  }
  return true
})

export default router
