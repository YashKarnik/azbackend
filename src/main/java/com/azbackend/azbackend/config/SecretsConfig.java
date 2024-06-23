package com.azbackend.azbackend.config;

import com.azbackend.azbackend.Pojos.SecretsModel;
import com.azure.identity.ClientSecretCredential;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecretsConfig {

    @Value("${key-vault.name}")
    private String keyVaultName;

    @Value("${key-vault.secrets.key.database_url}")
    private String keyVaultDatabaseUrlKey;
    @Value("${key-vault.secrets.key.database_username}")
    private String keyVaultDatabaseUsernameKey;
    @Value("${key-vault.secrets.key.database_password}")
    private String keyVaultDatabasePasswordKey;
    @Value("${key-vault.secrets.key.redis_access_key}")
    private String keyVaultRedisSASTokenKey;

    private final ClientSecretCredential clientSecretCredential;

    @Bean
    public SecretsModel getSecrets() {
        return getSecretsFromKKeyVault();
    }

    private SecretsModel getSecretsFromKKeyVault() {
        String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUri)
                .credential(clientSecretCredential)
                .buildClient();
        KeyVaultSecret postgresServer = secretClient.getSecret(keyVaultDatabaseUrlKey);
        KeyVaultSecret postgresUsername = secretClient.getSecret(keyVaultDatabaseUsernameKey);
        KeyVaultSecret postgresDbPassword = secretClient.getSecret(keyVaultDatabasePasswordKey);
        KeyVaultSecret keyVaultRedisAccessKey = secretClient.getSecret(keyVaultRedisSASTokenKey);

        SecretsModel secretsModel = new SecretsModel(postgresUsername.getValue(), postgresDbPassword.getValue(), postgresServer.getValue(), keyVaultRedisAccessKey.getValue());
        return secretsModel;
    }
}
