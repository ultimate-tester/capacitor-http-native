package is.brv.capacitor.http_native;

import android.net.Uri;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@CapacitorPlugin(name = "HTTP")
public class HTTPPlugin extends Plugin {

    @Override
    public void load() {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    private JSObject lowercaseHeaders(JSObject headers) {
        JSObject newHeaders = new JSObject();
        for (Iterator<String> iterator = headers.keys(); iterator.hasNext(); ) {
            String key = iterator.next();

            newHeaders.put(key.toLowerCase(), headers.getString(key));
        }

        return newHeaders;
    }

    private String stringifyObject(JSObject obj) {
        ArrayList<String> output = new ArrayList<>();

        for (Iterator<String> iterator = obj.keys(); iterator.hasNext(); ) {
            String key = iterator.next();

            try {
                output.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(obj.getString(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        StringBuilder stringify = new StringBuilder();
        for (int i = 0; i < output.size(); i++) {
            stringify.append(output.get(i));

            if (i < output.size() - 1) {
                stringify.append("&");
            }
        }

        return stringify.toString();
    }

    private String generateMultiPartBody(String boundary, JSObject obj) {
        StringBuilder body = new StringBuilder();

        for (Iterator<String> iterator = obj.keys(); iterator.hasNext(); ) {
            String key = iterator.next();

            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n");
            //body.append("Content-Type: text/plain; charset=utf8\r\n");
            body.append("\r\n");
            body.append(obj.getString(key, ""));
            body.append("\r\n");
        }

        body.append("--").append(boundary).append("--\r\n");

        return body.toString();
    }

    @PluginMethod
    public void request(final PluginCall call) {
        JSObject ret = new JSObject();

        String url = call.getString("url", "");
        String method = call.getString("method", "GET");
        JSObject query = call.getObject("query");
        JSObject headers = this.lowercaseHeaders(call.getObject("headers"));

        String stringBody = call.getString("body", null);
        JSObject objectBody = null;

        if (stringBody == null) {
            objectBody = call.getObject("body", null);
        }

        if (objectBody != null) {
            if (Objects.equals(headers.getString("content-type", null), "multipart/form-data")) {
                String boundary = UUID.randomUUID().toString().replace("-", "");
                stringBody = this.generateMultiPartBody(boundary, objectBody);
                headers.put("content-type", "multipart/form-data; boundary=" + boundary);
            } else {
                stringBody = this.stringifyObject(objectBody);
                headers.put("content-type", "application/x-www-form-urlencoded");
            }
        }

        try {
            Uri androidUri = Uri.parse(url);
            Uri.Builder androidUriBuilder = androidUri.buildUpon();

            if (query != null) {
                for (Iterator<String> iterator = query.keys(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    androidUriBuilder.appendQueryParameter(key, query.getString(key));
                }
            }

            URL u = new URL(androidUriBuilder.build().toString());

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();

                for (Iterator<String> iterator = headers.keys(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    urlConnection.setRequestProperty(key, headers.getString(key));
                }

                urlConnection.setRequestMethod(method);
                if (Objects.equals(method, "POST") && stringBody != null) {
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);

                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = stringBody.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                }

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    StringBuilder textBuilder = new StringBuilder();
                    try (Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        int c;
                        while ((c = reader.read()) != -1) {
                            textBuilder.append((char) c);
                        }
                    }

                    ret.put("data", textBuilder.toString());
                    ret.put("status", urlConnection.getResponseCode());
                    ret.put("url", urlConnection.getURL().toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            call.resolve(ret);
        }
    }
}
