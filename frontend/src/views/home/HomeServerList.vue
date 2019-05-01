<template>
    <div class="p2">
        <h4 class="mb1">{{ "katan.home.servers" | locale }}</h4>
        <transition name="fade">
            <transition-group name="list" v-for="server in serverList" tag="ul" class="server-list">
                <router-link :to="{ name: 'server', params: { serverId: server.id } }" tag="li" class="server" v-bind:key="server.id">
                    <div class="name">{{ server.id }}</div>
                </router-link>
            </transition-group>
        </transition>
        <div class="flex flex-center mt2 server-links">
            <a href="#">{{ "katan.home.create-server" | locale }}</a>
            <a href="javascript:void(0)" @click="refreshServers">{{ "katan.home.refresh" | locale }}</a>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class HomeServerList extends Vue {
        serverList: Array<any> = [];

        private refreshServers() {
            this.serverList = [];
            Vue.prototype.$http({
                method: "GET",
                url: "/listServers"
            }).then((result: any) => {
                if (result.status === 200) {
                    this.serverList = result.data.message;
                }
            });
        }

        created() {
            this.refreshServers();
        }
    }
</script>
<style lang="scss" scoped>
    .server-list {
        list-style-type: none;
        .server {
            cursor: pointer;
            color: initial;
            background-color: #FFF;
            padding: 12px;
            font-weight: 500;
            border: 2px solid transparent;
            border-radius: .4rem;
            -webkit-transition: all 200ms;
            -moz-transition: all 200ms;
            -ms-transition: all 200ms;
            -o-transition: all 200ms;
            transition: all 200ms;

            &:hover {
                border-color: rgba(82, 148, 226, .47);
            }

            .name {
                color: #5294e2;
                font-size: 18px;
            }

            .state {
                margin-top: .5rem;
                font-size: 12px;
                font-weight: 400;
                color: #383c4a;
            }
        }
    }

    .server-links {
        a {
            text-decoration: none;
            font-size: 16px;
            font-weight: 500;

            &:not(:last-child) {
                margin-right: .5rem;
                &:after {
                    content: '•';
                    margin-left: .5rem;
                    opacity: .47;
                }
            }
        }
    }
</style>
