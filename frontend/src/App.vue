<template>
  <div id="app">
    <transition name="fade" mode="out-in">
      <router-view />
    </transition>
  </div>
</template>
<style src="@/assets/scss/main.scss" lang="scss"></style>
