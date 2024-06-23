package com.azbackend.azbackend;

import com.azbackend.azbackend.Pojos.Models.RedisPostRequestModel;
import com.azure.identity.ClientSecretCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final ClientSecretCredential clientSecretCredential;
    private final Jedis jedis;

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        long startTime = System.nanoTime();
        String x = jedis.get(key);
        long endTime = System.nanoTime();
        return x + " --> Time Taken=> " + (endTime - startTime) + " nanoSeconds";
    }

    @GetMapping("/get-all-keys")
    public List<RedisPostRequestModel> getAllKeys() {
        Set<String> allKeys = jedis.keys("*");
        return allKeys.stream().map(e -> new RedisPostRequestModel(e, null)).toList();

    }

    @PostMapping("/set")
    public ResponseEntity add(@RequestBody RedisPostRequestModel request) {
        jedis.set(request.getKey(), request.getValue());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/incr")
    public ResponseEntity incr(@RequestBody RedisPostRequestModel request) {
        jedis.incr(request.getKey());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decr")
    public ResponseEntity decr(@RequestBody RedisPostRequestModel request) {
        jedis.decr(request.getKey());
        return ResponseEntity.ok().build();
    }

}
