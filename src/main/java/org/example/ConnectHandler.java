package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

public class ConnectHandler implements RequestHandler<HTTPRequest, HTTPResponse> {
    private static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoDbClient.builder().build();

    @Override
    public HTTPResponse handleRequest(HTTPRequest httpRequest, Context context) {
        String loginToken = null;
        if(httpRequest.getHeaders() != null && httpRequest.getHeaders().containsKey("Login-Token")) {
            loginToken = httpRequest.getHeaders().get("Login-Token");
        }

        String connectionId = null;
        if(httpRequest.getRequestContext() != null && httpRequest.getRequestContext().containsKey("connectionId")) {
            connectionId = httpRequest.getRequestContext().get("connectionId").toString();
        }
        int userId = authenticate(loginToken);

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("connections")
                .item(Map.of(
                        "UserId", AttributeValue.builder().s(String.valueOf(userId)).build(),
                        "ConnectionId", AttributeValue.builder().s(connectionId).build()))
                .build();

        DYNAMO_DB_CLIENT.putItem(putItemRequest);


        return HTTPResponse.builder()
                .statusCode(200)
                .body("Welcome user " + userId + "!")
                .build();
    }

    private int authenticate(String loginToken) {
        if ("123".equals(loginToken)) {
            return 1;
        } else if ("456".equals(loginToken)) {
            return 2;
        } else {
            throw new RuntimeException();
        }
    }
}
