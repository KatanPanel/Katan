<template>
  <div class="right-side flex-child p2">
    <h4 class="mb1">{{ "katan.home.create-server" | locale }}</h4>
    <form
      class="form mt2"
      enctype="multipart/form-data"
      method="post"
      accept-charset="UTF-8"
      @submit="submitServerCreation"
    >
      <div>
        <div class="group">
          <label for="server-name">Server name</label>
          <input
            id="server-name"
            v-model="form.serverName"
            type="text"
            class="input"
            required
          />
        </div>
        <div class="flex">
          <div class="group flex-child">
            <label for="server-address">Address</label>
            <input
              id="server-address"
              v-model="form.address"
              type="text"
              class="input"
              placeholder="0.0.0.0"
            />
          </div>
          <div class="group ml1">
            <label for="server-port">Port</label>
            <input
              id="server-port"
              v-model="form.port"
              type="number"
              min="1000"
              class="input"
              placeholder="25565"
            />
          </div>
        </div>
      </div>
      <div class="mt2">
        <div class="flex">
          <div class="group">
            <label for="server-player-slots">Server slots</label>
            <input
              id="server-player-slots"
              v-model="form.slots"
              type="number"
              min="1"
              class="input"
              required
            />
          </div>
          <div class="group flex-child ml1">
            <label for="server-memory">Memory (MB)</label>
            <input
              id="server-memory"
              v-model="form.memory"
              type="number"
              min="256"
              class="input"
              placeholder="1024"
              required
            />
          </div>
        </div>
      </div>
      <div class="group mt2">
        <div class="flex flex-row flex-end">
          <!-- Overwrite .flex-column from "group" -->
          <button
            type="button"
            class="btn full mr1"
            @click="$bus.emit('set-home-view', 'HomeServerList')"
          >
            Back
          </button>
          <button type="submit" class="btn info full">Create server</button>
        </div>
      </div>
    </form>
  </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component
export default class HomeCreateServer extends Vue {
  readonly form: object = {
    serverName: "",
    address: "0.0.0.0",
    port: 25565,
    slots: "",
    memory: 1024
  };

  private submitServerCreation(e: Event) {
    e.preventDefault();
    this.$http({
      method: "POST",
      url: "/createServer",
      data: this.form
    })
      .then((result: any) => {
        console.log(result);
      })
      .catch((error: any) => {
        console.error(error);
      });
  }
}
</script>
<style lang="scss" scoped>
.right-side {
  background-color: rgba(0, 0, 0, 0.1) !important;
}
</style>
