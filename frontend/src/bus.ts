import { EventEmitter } from "events";

export default class Bus extends EventEmitter {
  constructor() {
    super();
  }
}
