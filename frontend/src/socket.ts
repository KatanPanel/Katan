import { EventEmitter } from "events";
import Logger from "@/logger";

export default class Socket extends EventEmitter {
  private readonly logger: Logger = new Logger("GatewaySocket");
  private readonly endpoint: string;

  private client!: WebSocket;
  private state: SocketState = SocketState.DISCONNECTED;

  constructor(endpoint: string) {
    super();
    this.endpoint = endpoint;
  }

  connect() {
    if (this.state == SocketState.CONNECTED) return;

    this.client = new WebSocket(this.endpoint);
    this.client.onopen = () => {
      this.emit("connect");
      this.logger.info("[CONNECT]");
      this.state = SocketState.CONNECTED;
    };
    this.client.onclose = () => {
      this.emit("disconnect");
      this.logger.info("[DISCONNECT]");
      this.state = SocketState.DISCONNECTED;
    };
    this.client.onmessage = (data: any) => {
      this.emit("message", JSON.parse(data.data));
    };
  }

  disconnect() {
    if (this.state == SocketState.DISCONNECTED) return;

    this.client.close();
  }

  send(message: any) {
    this.client.send(JSON.stringify(message));
  }
}

enum SocketState {
  CONNECTED,

  DISCONNECTED
}
