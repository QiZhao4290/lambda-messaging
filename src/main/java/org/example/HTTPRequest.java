package org.example;

import lombok.Data;

import java.util.Map;

@Data
public class HTTPRequest {
    private Map<String, String> queryStringParameters;
    private Map<String, String> headers;
    private Map<String, Object> requestContext;
}
