<template>
    <main class="main">
        <div class="left-side">
            <div class="logo-container flex flex-center">
                <div class="logo">
                    K
                </div>
                <p class="logo-description">
          <span>{{ "katan.home.welcome" | locale }}</span
          ><br/>
                    {{ "katan.home.welcome.subtitle" | locale }}
                </p>
            </div>
            <footer class="footer flex">
                <div class="flex-start flex-child">
                    <a href="https://github.com/DevNatan/KaTan/releases"
                    >{{ "katan.home.version" | locale }}: 1.0.0-ALPHA</a
                    >
                </div>
                <div class="flex-end">
                    <a href="https://github.com/DevNatan/KaTan">Github</a>
                    <a href="https://github.com/DevNatan/KaTan/wiki">Developer API</a>
                </div>
            </footer>
        </div>
        <transition name="fade" mode="out-in">
            <keep-alive>
                <component :is="currentView"/>
            </keep-alive>
        </transition>
    </main>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import HomeServerList from "@/views/home/HomeServerList.vue";
    import HomeCreateServer from "@/views/home/HomeCreateServer.vue";

    @Component({
        components: {
            HomeServerList,
            HomeCreateServer
        }
    })
    export default class Home extends Vue {
        currentView: string = "HomeServerList";

        created() {
            this.$bus.on("set-home-view", (view: string) => (this.currentView = view));
        }
    }
</script>
<style lang="scss" scoped>
    .logo-container {
        -webkit-transform: translateY(-50%);
        -moz-transform: translateY(-50%);
        -ms-transform: translateY(-50%);
        -o-transform: translateY(-50%);
        transform: translateY(-50%);
    }

    .logo {
        font-size: 8em;
        font-family: "Titillium Web", sans-serif;
        display: flex;
        align-items: center;
        justify-content: center;
        text-shadow: 1px 1px 1px;
        background-color: #5294e2;
        color: #fff;
        padding: 2rem 4rem;
    }

    .logo-description {
        color: #ccc;
        font-size: 18px;
        font-weight: 500;
        margin-left: 2rem;

        span {
            display: inline-block;
            color: #fff;
            font-size: 26px;
            font-weight: 600;
            margin-bottom: 1rem;
            text-transform: uppercase;
        }
    }

    .left-side {
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 4rem;
        position: relative;

        .footer {
            position: absolute;
            bottom: 0;
            margin-bottom: 1rem;
            width: 100%;

            a {
                text-decoration: none;
                text-transform: uppercase;
                opacity: 0.47;
                letter-spacing: 1px;
                font-weight: 500;

                &:not(:last-child) {
                    margin-right: 1rem;

                    &::after {
                        content: "•";
                        margin-left: 1rem;
                        opacity: 0.47;
                    }
                }
            }
        }
    }

    .right-side {
        background-color: #5294e2;
        max-width: 50%;

        .btn {
            border-radius: 0;
            background-color: #fff;
            color: #5294e2;
            font-size: 14px;
            font-weight: 600;

            &:not(:last-child) {
                margin-right: 0.5rem;
            }
        }
    }

    .links {
        a {
            font-weight: 600;
            font-size: 18px;
            text-decoration: none;

            &:not(:last-child) {
                margin-right: 1rem;

                &::after {
                    content: "|";
                    margin-left: 1rem;
                    opacity: 0.47;
                }
            }
        }
    }

    .skew {
        background-color: #5294e2;
        position: absolute;
        top: 0;
    }
</style>
