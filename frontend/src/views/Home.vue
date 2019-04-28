<template>
  <div>
    <TopHeader />
    <h1>Home</h1>
    <LeftSidebar />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import TopHeader from "@/components/static/TopHeader.vue";
import LeftSidebar from "@/components/static/LeftSidebar.vue";

@Component({
  components: {
    TopHeader,
    LeftSidebar
  }
})
export default class Home extends Vue {}
</script>
