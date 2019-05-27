import Bus from "@/bus";
import Socket from "@/socket";
import Utils from "@/utils";
import {AxiosInstance} from "axios";

declare module "vue/types/vue" {

    interface Vue {
        $bus: Bus;
        $socket: Socket;
        $http: AxiosInstance;
        $utils: Utils
    }

}
