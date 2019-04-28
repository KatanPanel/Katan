<template>
  <main class="main">
    <Menu />
    <div class="inner">
      <header class="header">
        <h2>Heading</h2>
      </header>
      <div class="content">
        <h4>Content</h4>
      </div>
    </div>
  </main>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Header from "@/components/static/Header.vue";
import Menu from "@/components/static/Menu.vue";

@Component({
  components: {
    Header,
    Menu
  }
})
export default class Home extends Vue {}
</script>
