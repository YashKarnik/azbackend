package com.azbackend.azbackend.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

import com.azbackend.azbackend.Pojos.SecretsModel;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class SecretsConfig {

    @Value("${db.postgres.password}")
    private String password;

    @Value("${db.postgres.username}")
    private String username;
    @Value("${db.postgres.url}")
    private String url;


    @Bean
    public SecretsModel getSecrets() {
        try {
            return getSecretsFromKKeyVault();
        }
        catch(Exception e) {
            SecretsModel secretsModel = new SecretsModel(username, password, url);
            return secretsModel;
        }

    }

    //TODO: add key vault config
    private SecretsModel getSecretsFromKKeyVault() {
        String keyVaultName = "testvaultyash2000";
        String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUri)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        KeyVaultSecret postgresServer = secretClient.getSecret("postgresServer");
        KeyVaultSecret postgresUsername = secretClient.getSecret("postgresUsername");
        KeyVaultSecret postgresDbPassword = secretClient.getSecret("postgresDbPassword");
        SecretsModel secretsModel = new SecretsModel(postgresUsername.getValue(), postgresDbPassword.getValue(), postgresServer.getValue());
        return secretsModel;
    }

//    private String getStoredValue() {
//        String keyVaultName = "testvaultyash2000";
//        String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";
//        SecretClient secretClient = new SecretClientBuilder()
//                .vaultUrl(keyVaultUri)
//                .credential(new DefaultAzureCredentialBuilder().build())
//                .buildClient();
//        KeyVaultSecret postgresServer = secretClient.getSecret("postgresServer");
//        KeyVaultSecret postgresUsername = secretClient.getSecret("postgresUsername");
//        KeyVaultSecret postgresDbPassword = secretClient.getSecret("postgresDbPassword");
//        return storedSecret.getValue();
//    }
}
