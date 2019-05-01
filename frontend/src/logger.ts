export default class Logger {

    private readonly prefix: string;

    constructor(prefix: string) {
        this.prefix = prefix;
    }

    log(message?: any, ...params: any[]) {
        this.formatLog("LOG", message, params);
    }

    info(message?: any, ...params: any[]) {
        this.formatLog("INFO", message, params);
    }

    warn(message?: any, ...params: any[]) {
        this.formatLog("WARN", message, params);
    }

    error(message?: any, ...params: any[]) {
        this.formatLog("ERROR", message, params);
    }

    trace(message?: any, ...params: any[]) {
        this.formatLog("TRACE", message, params);
    }

    private formatLog(level: string, message?: any, ...params: any[]) {
        switch (level) {
            case "LOG":
                console.log("%c[" + this.prefix + "] %c" + message, "color: #576574; font-weight: bold", "color: black");
                break;
            case "INFO":
                console.info("%c[" + this.prefix + "] %c" + message, "color: #54a0ff; font-weight: bold", "color: black");
                break;
            case "WARN":
                console.warn("%c[" + this.prefix + "] %c" + message, "color: #ff9f43; font-weight: bold", "color: black");
                break;
            case "ERROR":
                console.error("%c[" + this.prefix + "] %c" + message, "color: #ff6b6b; font-weight: bold", "color: black");
                break;
            case "TRACE":
                console.trace("%c[" + this.prefix + "] %c" + message, "color: #5f27cd; font-weight: bold", "color: black");
                break;
        }
    }

}
