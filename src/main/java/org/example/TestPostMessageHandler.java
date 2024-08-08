package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.Map;

public class TestPostMessageHandler implements RequestHandler<PostMessageRequest, Void> {

    private static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoDbClient.builder()
            .build();
    private static final ApiGatewayManagementApiClient API_GATEWAY_MANAGEMENT_API_CLIENT =
            ApiGatewayManagementApiClient.builder()
                    .endpointOverride(URI.create("https://2hxwvtmbo6.execute-api.ap-northeast-2.amazonaws.com/production/"))
                    .build();

    @Override
    public Void handleRequest(PostMessageRequest postMessageRequest, Context context) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("connections")
                .keyConditions(
                        Map.of("UserId", Condition.builder()
                                .comparisonOperator(ComparisonOperator.EQ)
                                .attributeValueList(AttributeValue.builder().s(String.valueOf(postMessageRequest.getUserId())).build())
                                .build())
                )
                .build();
        QueryResponse queryResponse = DYNAMO_DB_CLIENT.query(queryRequest);
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            String connectionId = item.get("ConnectionId").s();
            PostToConnectionRequest postToConnectionRequest = PostToConnectionRequest.builder()
                    .connectionId(connectionId)
                    .data(SdkBytes.fromUtf8String(postMessageRequest.getContent()))
                    .build();
            try {
                API_GATEWAY_MANAGEMENT_API_CLIENT.postToConnection(postToConnectionRequest);
            } catch (GoneException goneException) {
                DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                        .tableName("connections")
                        .key(Map.of(
                                "UserId", AttributeValue.builder().s(String.valueOf(postMessageRequest.getUserId())).build(),
                                "ConnectionId", AttributeValue.builder().s(connectionId).build())
                        )
                        .build();
                DYNAMO_DB_CLIENT.deleteItem(deleteItemRequest);
            }
        }
        return null;
    }
}
