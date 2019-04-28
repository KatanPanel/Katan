<template>
  <main class="main" v-if="isServerLoaded">
    <Menu />
    <div class="inner">
      <header class="header">
        <h4>{{ serverId }}</h4>
        <p>
          <span><i data-feather="x"></i>{{ server.state === "STOPPED" ? "Offline" : "Online" }}</span>
          <span> • </span>
          <span><i data-feather="play"></i>0/100</span>
        </p>
      </header>
      <div class="m2">
        <router-view/>
      </div>
    </div>
  </main>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import Header from "@/components/static/Header.vue";
import Menu from "@/components/static/Menu.vue";

@Component({
  components: {
    Header,
    Menu
  }
})
export default class Server extends Vue {
  private serverId!: any;
  private server: any;

  private isServerLoaded: boolean = false;

  created() {
    this.serverId = this.$route.params.serverId;
    this.searchServer();
  }

  private searchServer() {
      Vue.prototype.$http({
          method: "GET",
          url: "server/" + this.serverId
      }).then((response: any) => {
          if (response.status === 200) {
              this.server = response.data.message;
              this.isServerLoaded = true;
              console.log(this.server);
          } else {
              // TODO: show error
          }
      }).catch((error: any) => {
          // TODO: redirect to server not found
      });
  }
}
</script>
