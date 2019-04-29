import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import Axios from "axios";
import Socket from "@/socket";
import Bus from "@/bus";

Vue.config.productionTip = false;

const vm = Vue.prototype;

vm.$http = Axios.create({ baseURL: "http://localhost:8081/" });
vm.$bus = new Bus();
vm.$socket = new Socket("ws://localhost:8081/");
vm.$socket.connect();

new Vue({
  router,
  render: h => h(App)
}).$mount("#app");
