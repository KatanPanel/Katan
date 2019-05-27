<template>
    <main class="main" v-if="isServerLoaded">
        <Menu/>
        <div class="inner">
            <header class="header">
                <h3>{{ server.name }}</h3>
                <div class="flex flex-center mt1" style="opacity: .7">
                    <div class="label">
                        {{ server.query.players }}/{{ server.query.maxPlayers }} players online
                    </div>
                    <div class="label ml1">
                        <BarChartIcon width="18px" height="18px"/>
                        {{ server.query.latency }}ms
                    </div>
                </div>
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

    import {BarChartIcon, CheckIcon, PlayIcon, XIcon} from "vue-feather-icons/icons";

    @Component({
        components: {
            BarChartIcon,
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

        created() {
            this.serverId = this.$route.params.serverId;
            this.searchServer(() => {
                this.isServerLoaded = true;
                this.$socket.on("message", (data: any) => {
                    switch (data.type) {
                        case "server-updated" && "pong": {
                            console.log("Server updated");
                            this.server = data.content.server;
                            break
                        }
                        case "server-log": {
                            this.$bus.emit("server-log", data);
                            break;
                        }
                    }
                });

                setInterval(() => {
                    this.$socket.send({
                        type: "command",
                        content: {
                            command: "ping-server",
                            server: this.server.id
                        }
                    })
                }, 5001)
            });
        }

        private searchServer(callback: () => void) {
            this.$http({
                method: "GET",
                url: "servers/" + this.serverId
            })
                .then((response: any) => {
                    this.server = response.data.content;
                    callback();
                })
                .catch((error: any) => {
                    console.error(error);
                });
        }
    }
</script>
