import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/index.scss'

// Element Plus 组件/API 由 unplugin-vue-components + unplugin-auto-import 按需引入，
// 中文 locale 在 App.vue 的 el-config-provider 中应用。
const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
