<template>
  <main class="main" v-if="isServerLoaded">
    <Menu />
    <div class="inner">
      <header class="header">
        <h4>{{ serverId }}</h4>
        <p>
          <span
            ><i data-feather="x"></i
            >{{ server.state === "STOPPED" ? "Offline" : "Online" }}</span
          >
          <span> • </span>
          <span
            ><i data-feather="play"></i
            >{{ server.query.players + "/" + server.query.maxPlayers }}</span
          >
        </p>
      </header>
      <div class="m2">
        <router-view :server="server" />
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
export default class Server extends Vue {
  private serverId!: any;
  private isServerLoaded: boolean = false;

  server?: any = null;

  created() {
    this.serverId = this.$route.params.serverId;
    this.searchServer(() => {
      const vm = Vue.prototype;
      vm.$socket.on("message", (data: any) => {
        if (data.reason == "server_updated") {
          this.server = data.content.server;
          return;
        }

        switch (data.type) {
          case "server-log": {
            Vue.prototype.$bus.emit("server-log", data);
            break;
          }
          default:
            console.error("Failed to handle server message: ", data);
        }
      });
    });
  }

  private searchServer(callback: () => void) {
    Vue.prototype
      .$http({
        method: "GET",
        url: "server/" + this.serverId
      })
      .then((response: any) => {
        if (response.status === 200) {
          this.server = response.data.message;
          this.isServerLoaded = true;
          callback();
        } else {
          // TODO: show error
        }
      })
      .catch((error: any) => {
        // TODO: redirect to server not found
      });
  }
}
</script>
