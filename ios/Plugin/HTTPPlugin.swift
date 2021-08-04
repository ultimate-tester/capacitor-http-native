import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(HTTPPlugin)
public class HTTPPlugin: CAPPlugin {

    func lowercaseHeaders(headers: JSObject) -> JSObject {
        var newHeaders = JSObject();
    
        for (key, val) in headers {
            newHeaders.updateValue(val, forKey: key.lowercased());
        }

        return newHeaders;
    }

     func stringifyObject(obj: JSObject) -> String {
        var output = [String]();

        for (key, value) in obj {
            output.append(key.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)! +
                            "=" +
                            (value as! String).addingPercentEncoding(withAllowedCharacters:.urlHostAllowed)!);
        }

        var stringify = "";
        for row in output {
            stringify.append(row);
            stringify.append("&");
        }

        return stringify;
    }

    func generateMultiPartBody(boundary: String, obj: JSObject) -> String {
        var body = "";

        for (key, value) in obj {
            body.append("--");
            body.append(boundary);
            body.append("\r\n");
            body.append("Content-Disposition: form-data; name=\"");
            body.append(key);
            body.append("\"\r\n");
            //body.append("Content-Type: text/plain; charset=utf8\r\n");
            body.append("\r\n");
            body.append(value as! String);
            body.append("\r\n");
        }

        body.append("--");
        body.append(boundary);
        body.append("--\r\n");

        return body;
    }

    @objc func request(_ call: CAPPluginCall) {
        let url = call.getString("url", "");
        let method = call.getString("method", "GET");
        let query = call.getObject("query", JSObject());
        var headers = self.lowercaseHeaders(headers: call.getObject("headers", JSObject()));

        var stringBody = call.getString("body", "");
        var objectBody = JSObject();

        if (stringBody == "") {
            objectBody = call.getObject("body", JSObject());
        }

        if (objectBody.isEmpty == false) {
            if (headers["content-type",default:""] as! String == "multipart/form-data") {
                let boundary = UUID.init().uuidString.replacingOccurrences(of: "-", with: "");
                stringBody = self.generateMultiPartBody(boundary: boundary, obj: objectBody);
                headers.updateValue("content-type", forKey: "multipart/form-data; boundary=" + boundary);
            } else {
                stringBody = self.stringifyObject(obj:objectBody);
                headers.updateValue("content-type", forKey: "application/x-www-form-urlencoded");
            }
        }
        
        var fullUrl = url;
        
        if(query.isEmpty == false) {
            fullUrl += "?"
        }
        
        for(key, value) in query {
            fullUrl.append(key.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!);
            fullUrl.append("=");
            fullUrl.append((value as! String).addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!);
        }
        
        var urlRequest = URLRequest(url: URL(string:url)!);
        urlRequest.httpMethod = method;
        
        for (key,value) in headers {
            urlRequest.addValue(value as! String, forHTTPHeaderField: key);
        }
        
        if(method == "POST") {
            urlRequest.httpBody = stringBody.data(using:.utf8);
        }

        let task = URLSession.shared.dataTask(with: urlRequest) {
            (data, response, error) in
            guard let data = data else { return }
            let httpResponse = response as! HTTPURLResponse;

            call.resolve([
                "data": String(data: data, encoding: .utf8) as Any,
                "status": httpResponse.statusCode,
                "url": httpResponse.url?.absoluteString as Any
            ])
        };

        task.resume()
    }
}
