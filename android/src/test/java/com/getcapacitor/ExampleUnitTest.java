package com.getcapacitor;

import static org.junit.Assert.*;

import is.brv.capacitor.http_native.HTTPPlugin;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void basicRequest() throws Exception {
        HTTPPlugin plugin = new HTTPPlugin();

        JSObject params = new JSObject();
        params.put("method", "GET");

        JSObject args = new JSObject();
        args.put("url", "https://httpbin.org/get");
        args.put("params", params);

        plugin.request(new PluginCall(null, "", "", "request", args));
    }
}
