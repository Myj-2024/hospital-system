import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 引入所有图标
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 引入中文包

import App from './App.vue'
import router from './router'
import store from './store' // 匹配你目录中的 store 文件夹
import './assets/css/global.css' // 引入全局样式（重置 margin 等）
// 新增：引入用户Store，初始化角色
import { useUserStore } from '@/store/modules/user'

const app = createApp(App)

// 全局注册 Element Plus 图标，方便在 Login 和 Sidebar 中直接使用
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(store)
app.use(router)
app.use(ElementPlus, {
    locale: zhCn, // 全局设置 Element Plus 组件为中文
})

// 新增：初始化用户角色（页面刷新后恢复角色信息）
// 需在 app.use(store) 之后执行，确保 Pinia 已初始化
const userStore = useUserStore()
userStore.initRole()

app.mount('#app')