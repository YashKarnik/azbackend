package com.azbackend.azbackend;

import com.azure.identity.ClientSecretCredential;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/storage-account-blob")
@RequiredArgsConstructor
public class BlobStorageController {


    @Value("${storage_account}")
    private String storageAccount;
    @Value("${storage_account.container_name}")
    private String containerName;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ClientSecretCredential clientSecretCredential;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(@RequestParam MultipartFile file) {
        try {
            String localPath = "./data/";
            new File(localPath).mkdirs();

            String fileName = "FILE_" + java.util.UUID.randomUUID() + "_" + file.getName() + ".png";
            Path path = Paths.get(localPath + fileName).toAbsolutePath();
            file.transferTo(path.toFile());
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .endpoint("https://" + storageAccount + ".blob.core.windows.net/")
                    .credential(clientSecretCredential)
                    .buildClient();
            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
            blobClient.uploadFromFile(localPath + fileName);
            Files.deleteIfExists(path);
        } catch (Exception e) {
            System.out.println(e);
        }
        return ResponseEntity.ok().build();
    }

}
