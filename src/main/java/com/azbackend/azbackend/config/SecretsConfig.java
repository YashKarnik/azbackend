package com.azbackend.azbackend.config;

import com.azbackend.azbackend.Pojos.SecretsModel;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretsConfig {
    @Bean
    public SecretsModel getSecrets() {
        return getSecretsFromKKeyVault();
    }

    private SecretsModel getSecretsFromKKeyVault() {
        String clientId = System.getenv("CLIENT_ID");
        String clientSecret = System.getenv("CLIENT_SECRET");
        String tenantId = System.getenv("TENANT_ID");
        System.out.println("clientId=" + clientId);
        System.out.println("clientSecret=" + clientSecret);
        System.out.println("tenantId=" + tenantId);
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(System.getenv("CLIENT_ID"))
                .clientSecret(System.getenv("CLIENT_SECRET"))
                .tenantId(System.getenv("TENANT_ID"))
                .build();

        String keyVaultName = System.getenv("KEY_VAULT");
        String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUri)
                .credential(clientSecretCredential)
                .buildClient();
        KeyVaultSecret postgresServer = secretClient.getSecret("flexiUrl");
        KeyVaultSecret postgresUsername = secretClient.getSecret("flexiUser");
        KeyVaultSecret postgresDbPassword = secretClient.getSecret("flexiPassword");
        SecretsModel secretsModel = new SecretsModel(postgresUsername.getValue(), postgresDbPassword.getValue(), postgresServer.getValue());
        return secretsModel;
    }
}
