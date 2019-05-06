<template>
    <div class="console">
        <div class="flex flex-center mb1">
            <h4 class="flex-child">Console</h4>
            <div>
                <button @click="startServer"
                        :class="server.state === 'RUNNING' || server.state === 'STARTING' ? 'disabled' : ''"
                        class="btn mr1">Start
                </button>
                <button @click="stopServer" :class="server.state === 'STOPPED' ? 'disabled' : ''" class="btn danger">
                    Stop server
                </button>
            </div>
        </div>
        <div class="box mb1 console-output" id="console-output">
            <p v-for="(log, index) in serverLogs" v-bind:key="'log-' + index" class="console-log list-item">
                <code>{{ log }}</code>
            </p>
        </div>
        <form class="form" @submit="inputServer">
            <div class="group">
                <input id="console-input" type="text"
                       :placeholder='commandHistory.length > 0 ? commandHistory[lastCommandIndex] : "Type \"/help\" for help."'>
            </div>
        </form>
    </div>
</template>
<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class ServerConsole extends Vue {
        private readonly SOCKET = Vue.prototype.$socket;
        private consoleOutputElement!: Element;

        serverLogs: Array<string> = Array();
        commandHistory: Array<string> = Array();
        lastCommandIndex: number = 0;

        get server(): any {
            return this.$attrs.server
        }

        private startServer(): void {
            if (this.server.state == "RUNNING" || this.server.state == "STARTING")
                return;

            this.SOCKET.send({
                type: "command",
                content: {
                    command: "start-server",
                    server: this.server.id
                }
            });
        }

        private stopServer(): void {
            if (this.server.state == "STOPPED")
                return;

            this.SOCKET.send({
                type: "command",
                content: {
                    command: "stop-server",
                    server: this.server.id
                }
            });
            this.commandHistory = Array();
        }

        private inputServer(e: Event): void {
            e.preventDefault();

            if (this.server.state == "RUNNING") {
                const el = (<Element>e.target).querySelector("input")!!;
                const value = el.value;
                if (value) {
                    const input = value.trim();
                    if (input.length > 0) {
                        el.value = "";
                        this.commandHistory.push(input);
                        if (this.commandHistory.length > 1)
                            this.lastCommandIndex++;
                        this.SOCKET.send({
                            type: "command",
                            content: {
                                command: "input-server",
                                server: this.server.id,
                                input: input
                            }
                        });
                    }
                }
            }
        }

        private scrollDownConsoleOutput(): void {
            const el = this.consoleOutputElement;
            el.scrollTop = el.scrollHeight;
        }

        created() {
            this.$http({
                method: "GET",
                url: "server/" + this.server.id + "/logs"
            }).then((response: any) => {
                if (response.status === 200) {
                    const logs = <Array<any>>response.data.message;
                    if (logs.length > 0) {
                        for (let log of logs) {
                            this.serverLogs.push(log);
                        }
                    }
                }
            }).catch((error: any) => {
                console.error(error);
            }).then(() => {
                // handle server-log received from "Server" component
                this.$bus.on("server-log", (data: any) => {
                    this.serverLogs.push(data.message);
                    setTimeout(() => this.scrollDownConsoleOutput(), 50);
                });

                this.scrollDownConsoleOutput();
            });
        }

        mounted() {
            this.consoleOutputElement = document.getElementById("console-output")!!;

            var consoleInput = <HTMLInputElement>document.getElementById("console-input")!!;
            document.onkeydown = (e: KeyboardEvent) => {
                if (this.commandHistory.length > 0 && e.keyCode == 38) {
                    this.lastCommandIndex++;
                    if (this.lastCommandIndex >= this.commandHistory.length)
                        this.lastCommandIndex = 0;

                    consoleInput.value = this.commandHistory[this.lastCommandIndex]
                }
            }
        }
    }
</script>
<style lang="scss" scoped>
    .console-output {
        max-height: 480px;
        min-height: 480px;
        overflow-y: auto;
        overflow-x: hidden;
        display: flex;
        flex-direction: column;

        &::-webkit-scrollbar {
            width: 8px;
            border-radius: .4rem;
        }

        &::-webkit-scrollbar-track {
            border-radius: .4rem;
            background-color: transparent;
        }

        &::-webkit-scrollbar-thumb {
            border-radius: .4rem;
            background: rgba(0, 0, 0, .2);
        }

        .console-log {
            color: #CCC;
        }
    }
</style>
