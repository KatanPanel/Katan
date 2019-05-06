import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import Axios from "axios";
import Socket from "@/socket";
import Bus from "@/bus";
import Language from "@/language";
import Utils from "@/utils";

const vm = Vue.prototype;
let $lang: Language;

Vue.config.productionTip = false;
vm.$http = Axios.create({baseURL: "http://localhost:8081/"});
vm.$bus = new Bus();
vm.$socket = new Socket("ws://localhost:8081/");
vm.$socket.connect();
vm.$utils = new Utils();

Vue.filter("locale", function (key: string) {
    let value = $lang.get(key);
    return value == null ? "[unknown key]" : value;
});

$lang = new Language(() => {
    new Vue({
        router,
        render: h => h(App)
    }).$mount("#app");
});
