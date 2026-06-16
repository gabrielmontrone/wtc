package com.wtc.attachment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;

@Service
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Presigner presigner;
    private final String endpoint;

    public S3Service(
            @Value("${aws.access-key}") String accessKey,
            @Value("${aws.secret-key}") String secretKey,
            @Value("${aws.s3.endpoint}") String endpoint) {

        this.endpoint = endpoint;
        this.presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // <--- O nome correto no Java é este
                        .build())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String generatePresignedUrl(String key, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(objectRequest)
                .build();

        return presigner.presignPutObject(presignRequest).url().toString();
    }

    /**
     * Builds the public (path-style) URL used to download/display the object once it
     * has been uploaded. Matches the path-style access configured for the presigner.
     */
    public String getPublicUrl(String key) {
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return base + "/" + bucketName + "/" + key;
    }
}