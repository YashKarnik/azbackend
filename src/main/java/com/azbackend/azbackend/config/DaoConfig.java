package com.azbackend.azbackend.config;

import com.azbackend.azbackend.Pojos.SecretsModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@RequiredArgsConstructor
public class DaoConfig {
    private final SecretsModel secretsModel;
    @Value("${azure.redis.host}")
    private String azureRedisHost;
    @Value("${postgres.driver}")
    private String postgresDriver;
    @Value("${azure.redis.port}")
    private Integer azureRedisPort;

    @Bean
    public NamedParameterJdbcTemplate mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        try {
            dataSource.setDriverClassName(postgresDriver);
            dataSource.setUrl(secretsModel.datasourceUrl());
            dataSource.setUsername(secretsModel.datasourceUsername());
            dataSource.setPassword(secretsModel.datasourcePassword());
        } catch (Exception e) {
            System.out.println(e);
        }
        return new NamedParameterJdbcTemplate(dataSource);
    }

//    @Bean
//    public Jedis setjedis() {
//        return new Jedis(azureRedisHost, azureRedisPort, DefaultJedisClientConfig.builder()
//                .password(secretsModel.redisAccessKey())
//                .ssl(true)
//                .build());
//    }

}
