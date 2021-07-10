import {WebPlugin} from '@capacitor/core';

import type {HTTPResponse} from './HTTPResponse';
import type {HTTPPlugin} from './definitions';

export class HTTPWeb extends WebPlugin implements HTTPPlugin {
    async request(url: string, params: Record<string, any>): Promise<HTTPResponse> {
        const response = await fetch(url, {
            method: params.method || 'GET',
            credentials: 'include',
            headers: params.headers,
            body: params.data
        });

        const data = await response.text();

        return {
            data,
            status: response.status,
            url: response.url,
        };
    }
}
