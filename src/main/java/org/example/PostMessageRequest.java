package org.example;

import lombok.Data;

@Data
public class PostMessageRequest {
    private int userId;
    private String content;
}
