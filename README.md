# http-native

Provides access to low level HTTP functions (get, post, etc.)

## Install

```bash
npm install http-native
npx cap sync
```

## API

<docgen-index>

* [`request(...)`](#request)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### request(...)

```typescript
request(url: string, params: any) => any
```

| Param        | Type                |
| ------------ | ------------------- |
| **`url`**    | <code>string</code> |
| **`params`** | <code>any</code>    |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### HTTPResponse

| Prop         | Type                |
| ------------ | ------------------- |
| **`data`**   | <code>string</code> |
| **`status`** | <code>number</code> |
| **`url`**    | <code>string</code> |

</docgen-api>
