import type {HTTPResponse} from "./HTTPResponse";

export interface HTTPPlugin {
    request(url:string, params: Record<string, any>): Promise<HTTPResponse>;
}
