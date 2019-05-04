import Bus from "@/bus";
import Socket from "@/socket";
import { AxiosInstance } from "axios";

declare module "vue/types/vue" {
  interface Vue {
    $bus: Bus;
    $socket: Socket;
    $http: AxiosInstance;
  }
}
