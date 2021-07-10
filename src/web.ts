import { WebPlugin } from '@capacitor/core';

import type { HTTPPlugin } from './definitions';

export class HTTPWeb extends WebPlugin implements HTTPPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
