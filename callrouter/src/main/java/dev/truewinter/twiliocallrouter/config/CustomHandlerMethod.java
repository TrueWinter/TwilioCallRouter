package dev.truewinter.twiliocallrouter.config;

import com.twilio.http.HttpMethod;

public enum CustomHandlerMethod {
    GET(HttpMethod.GET),
    POST(HttpMethod.POST);

    private HttpMethod method;
    CustomHandlerMethod(HttpMethod method) {
        this.method = method;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
