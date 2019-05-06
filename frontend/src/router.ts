import Vue from "vue";
import Router from "vue-router";
import Home from "@/views/Home.vue";
import Server from "@/components/server/Server.vue";
import ServerIndex from "@/views/server/ServerIndex.vue";
import ServerConsole from "@/views/server/ServerConsole.vue";
import ServerFTP from "@/components/server/ServerFTP.vue"
import ServerFTPUpload from "@/views/server/ServerFTPUpload.vue";
import ServerFTPFileList from "@/views/server/ServerFTPFileList.vue";

Vue.use(Router);

export default new Router({
    mode: "history",
    routes: [
        {
            path: "/",
            name: "home",
            component: Home
        },
        {
            path: "/server/:serverId/",
            component: Server,
            children: [
                {
                    path: "",
                    name: "server",
                    component: ServerIndex
                },
                {
                    path: "console",
                    name: "console",
                    component: ServerConsole
                },
                {
                    path: "ftp",
                    component: ServerFTP,
                    children: [
                        {
                            path: "",
                            name: "ftp",
                            component: ServerFTPFileList
                        },
                    ]
                },
                {
                    path: "ftp/upload",
                    name: "ftp-upload",
                    component: ServerFTPUpload
                }
            ]
        }
    ]
});
