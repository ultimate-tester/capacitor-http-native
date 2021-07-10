import { registerPlugin } from '@capacitor/core';

import type { HTTPPlugin } from './definitions';

const HTTP = registerPlugin<HTTPPlugin>('HTTP', {
  web: () => import('./web').then(m => new m.HTTPWeb()),
});

export * from './definitions';
export { HTTP };
