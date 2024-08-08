package org.example;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HTTPResponse {
    private int statusCode;
    private Object body;
}
