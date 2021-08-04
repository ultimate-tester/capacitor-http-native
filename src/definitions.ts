import type {HTTPRequestOptions} from "./HTTPRequestOptions";
import type {HTTPResponse} from "./HTTPResponse";

export interface HTTPPlugin {
    request(options: HTTPRequestOptions): Promise<HTTPResponse>;
}