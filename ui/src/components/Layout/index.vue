<template>
  <div class="layout-page">
    <el-container class="modern-layout">
      <Sidebar/>

      <el-container class="main-wrapper">
        <Header/>

        <el-main class="content-body">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component"/>
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import Sidebar from './sidebar.vue'
import Header from './header.vue'
</script>

<style scoped>
.layout-page {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.modern-layout {
  height: 100vh;
  background-color: #f4f7fe;
}

.main-wrapper {
  flex-direction: column;
  height: 100vh;
}

.content-body {
  padding: 24px;
  background-color: #f4f7fe;
  overflow-y: auto;
}

/* 路由过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>