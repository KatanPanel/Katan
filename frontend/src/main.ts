import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import Axios from "axios";
import Socket from "@/socket";
import Bus from "@/bus";
import Language from "@/language";

Vue.config.productionTip = false;

const vm = Vue.prototype;

Vue.filter('locale', function (key: string) {
  let value = vm.$lang.get(key);
  return value == null ? "[unknown key]" : value;
});

vm.$http = Axios.create({ baseURL: "http://localhost:8081/" });
vm.$bus = new Bus();
vm.$socket = new Socket("ws://localhost:8081/");
vm.$socket.connect();
vm.$lang = new Language(() => {
  new Vue({
    router,
    render: h => h(App)
  }).$mount("#app");
});
