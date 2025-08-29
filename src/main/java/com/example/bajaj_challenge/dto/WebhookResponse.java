package com.example.bajaj_challenge.dto;

import lombok.Data;

@Data
public class WebhookResponse {
    private String webhookUrl;
    private String accessToken;
}