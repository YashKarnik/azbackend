package com.azbackend.azbackend.config;

import com.azbackend.azbackend.Pojos.SecretsModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@RequiredArgsConstructor
public class DaoConfig {
    private final SecretsModel secretsModel;
    @Bean
    public NamedParameterJdbcTemplate mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(secretsModel.datasourceUrl());
        dataSource.setUsername(secretsModel.datasourceUsername());
        dataSource.setPassword(secretsModel.datasourcePassword());
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
