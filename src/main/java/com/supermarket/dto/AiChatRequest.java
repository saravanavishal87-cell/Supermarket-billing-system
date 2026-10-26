package com.supermarket.dto;

public class AiChatRequest {
    private String query;

    public AiChatRequest() {}

    public AiChatRequest(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
