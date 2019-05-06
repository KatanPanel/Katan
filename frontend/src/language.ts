import Vue from "vue";

export default class Language {
    private values!: any;

    constructor(callback: CallableFunction) {
        Vue.prototype
            .$http({
                method: "GET",
                url: "/locale"
            })
            .then((result: any) => {
                this.values = result.data.content;
                callback();
            });
    }

    get(key: string): string {
        return this.values[key];
    }

}
