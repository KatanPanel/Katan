<template>
  <router-view />
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";

@Component
export default class Server extends Vue {
  @Prop() private id!: string;
}
</script>
