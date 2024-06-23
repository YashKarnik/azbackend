package com.azbackend.azbackend.Pojos.Models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedisPostRequestModel {
    private String key;
    private String value;

}
