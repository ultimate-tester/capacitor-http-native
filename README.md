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
request(options: HTTPRequestOptions) => any
```

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#httprequestoptions">HTTPRequestOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### HTTPRequestOptions

| Prop          | Type                |
| ------------- | ------------------- |
| **`url`**     | <code>string</code> |
| **`method`**  | <code>string</code> |
| **`body`**    | <code>string</code> |
| **`query`**   | <code>any</code>    |
| **`headers`** | <code>any</code>    |


#### HTTPResponse

| Prop         | Type                |
| ------------ | ------------------- |
| **`data`**   | <code>string</code> |
| **`status`** | <code>number</code> |
| **`url`**    | <code>string</code> |

</docgen-api>
