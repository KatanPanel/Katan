import Vue from "vue";

export default class Language {
  private values!: any;

  constructor(callback: (result: any) => void) {
    this.init(result => {
      callback(result);
    });
  }

  get(key: string): any {
    return this.values[key];
  }

  init(callback: (result: any) => void) {
    Vue.prototype
      .$http({
        method: "GET",
        url: "/locale"
      })
      .then((result: any) => {
        this.values = result.data.content;
        callback(result);
      });
  }
}
