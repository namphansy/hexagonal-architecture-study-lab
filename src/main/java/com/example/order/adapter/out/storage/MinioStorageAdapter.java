package com.example.order.adapter.out.storage;

import com.example.order.application.port.out.FileStorageObject;
import com.example.order.application.port.out.FileStoragePort;
import com.example.order.application.port.out.StoredFileLocation;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Component;

@Component
public class MinioStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioStorageAdapter(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public StoredFileLocation store(FileStorageObject file) {
        try {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(file.getObjectName())
                    .stream(file.getContent(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return new StoredFileLocation(properties.getBucket(), file.getObjectName());
        } catch (Exception exception) {
            throw new MinioStorageException("Failed to store file in MinIO.", exception);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(properties.getBucket())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.getBucket())
                    .build());
        }
    }
}
