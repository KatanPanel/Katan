<template>
    <main class="main" v-if="isServerLoaded">
        <Menu/>
        <div class="inner">
            <header class="header">
                <h3>{{ server.name }}</h3>
                <p>
          <span>
            <XIcon v-if="server.state !== 'RUNNING'"/>
            <CheckIcon v-else/> Server is {{ server.state }}
          </span>
                </p>
            </header>
            <div class="m2">
                <router-view :server="server"/>
            </div>
        </div>
    </main>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import Header from "@/components/static/Header.vue";
    import Menu from "@/components/static/Menu.vue";
    // @ts-ignore
    import {CheckIcon, PlayIcon, XIcon} from "vue-feather-icons/icons";

    @Component({
        components: {
            CheckIcon,
            PlayIcon,
            XIcon,
            Header,
            Menu
        }
    })
    export default class Server extends Vue {
        private serverId!: any;
        private isServerLoaded: boolean = false;

        server?: any = null;

        get getServerMemory() {
            return this.server.initParams.toString().match("Xmx(.*\\d)M")[0];
        }

        created() {
            this.serverId = this.$route.params.serverId;
            this.searchServer(() => {
                this.$socket.on("message", (data: any) => {
                    if (data.reason == "server_updated") {
                        this.server = data.content.server;
                        return;
                    }

                    switch (data.type) {
                        case "server-log": {
                            this.$bus.emit("server-log", data);
                            break;
                        }
                        default:
                            console.error("Failed to handle server message: ", data);
                    }
                });
            });
        }

        private searchServer(callback: () => void) {
            this.$http({
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
