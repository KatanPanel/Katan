import Vue from "vue";
import Router from "vue-router";
import Home from "@/views/Home.vue";
import Server from "@/components/server/Server.vue";
import ServerIndex from "@/views/server/ServerIndex.vue";
import ServerConsole from "@/views/server/ServerConsole.vue";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/",
      name: "home",
      component: Home
    },
    {
      path: "/server",
      name: "server",
      component: Server,
      children: [
        {
          path: "",
          name: "index",
          component: ServerIndex
        },
        {
          path: "console",
          name: "console",
          component: ServerConsole
        }
      ]
    }
  ]
});
