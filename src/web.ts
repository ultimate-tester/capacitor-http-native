import {WebPlugin} from '@capacitor/core';

import type {HTTPRequestOptions} from "./HTTPRequestOptions";
import type {HTTPResponse} from './HTTPResponse';
import type {HTTPPlugin} from './definitions';

export class HTTPWeb extends WebPlugin implements HTTPPlugin {
    static lowercaseHeaders(headers: Record<string, string>): Record<string, string> {
        for (const property in headers) {
            if (Object.prototype.hasOwnProperty.call(headers, property) === false) {
                continue;
            }

            const oldValue = headers[property];
            delete headers[property];
            headers[property.toLowerCase()] = oldValue;
        }

        return headers;
    }

    static stringifyObject(obj: Record<string, any>): string {
        const output = [];
        for (const property in obj) {
            if (Object.prototype.hasOwnProperty.call(obj, property) === false) {
                continue;
            }

            const encodedKey = encodeURIComponent(property);
            const encodedValue = encodeURIComponent(obj[property]);
            output.push(`${encodedKey}=${encodedValue}`);
        }

        return output.join('&');
    }

    async request(options: HTTPRequestOptions): Promise<HTTPResponse> {
        let url: string = options.url || '';
        const method: string = options.method || 'GET';
        let body: any = options.body || null;
        const query: Record<string, any> = options.query || {};
        const headers: Record<string, any> = HTTPWeb.lowercaseHeaders(options.headers || {});

        if (body !== null && typeof (body) === 'object') {
            if (Object.prototype.hasOwnProperty.call(headers, 'content-type') && headers['content-type'] === 'multipart/form-data') {
                // Convert object back to FormData, let Fetch handle the multipart/form-data requests
                const formData = new FormData();
                const keys = Object.keys(body);
                for (const key of keys) {
                    formData.append(key, body[key]);
                }

                body = formData;
                delete headers['content-type'];
            } else {
                body = HTTPWeb.stringifyObject(body);
                headers['content-type'] = 'application/x-www-form-urlencoded';
            }
        }

        if (typeof (query) === 'object') {
            const qs = HTTPWeb.stringifyObject(query);
            if (qs.length > 0) {
                url += '?' + qs;
            }
        }

        const response = await fetch(url, {
            method: method,
            credentials: 'include',
            headers: headers,
            body: body
        });

        const data = await response.text();

        return {
            data,
            status: response.status,
            url: response.url,
        };
    }
}
