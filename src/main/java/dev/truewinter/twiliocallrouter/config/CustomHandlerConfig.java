package dev.truewinter.twiliocallrouter.config;

public class CustomHandlerConfig {
    private String url;
    private CustomHandlerMethod method;

    CustomHandlerConfig(String url, CustomHandlerMethod method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public CustomHandlerMethod getMethod() {
        return method;
    }
}
