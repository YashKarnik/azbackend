package com.azbackend.azbackend.Pojos;

public record SecretsModel(String datasourceUsername, String datasourcePassword, String datasourceUrl,
                           String redisAccessKey) {
}
