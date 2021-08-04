export interface HTTPRequestOptions {
    url: string;
    method: string;
    body: string;
    query: Record<string, any>;
    headers: Record<string, any>;
}