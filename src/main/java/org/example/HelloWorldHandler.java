package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloWorldHandler implements RequestHandler<HTTPRequest, String> {
    @Override
    public String handleRequest(HTTPRequest httpRequest, Context context) {
        String name = new String();
        if(httpRequest.getQueryStringParameters() != null) {
            name = httpRequest.getQueryStringParameters().getOrDefault("name", "default");
        }
        return "Hello " + name + "!";
    }
}
