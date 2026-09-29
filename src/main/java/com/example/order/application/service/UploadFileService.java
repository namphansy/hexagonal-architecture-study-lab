package com.example.order.application.service;

import com.example.order.application.port.in.UploadFileCommand;
import com.example.order.application.port.in.UploadFileResult;
import com.example.order.application.port.in.UploadFileUseCase;
import com.example.order.application.port.out.FileStorageObject;
import com.example.order.application.port.out.FileStoragePort;
import com.example.order.application.port.out.StoredFileLocation;

import java.util.UUID;
import java.util.function.Supplier;

public class UploadFileService implements UploadFileUseCase {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final FileStoragePort fileStoragePort;
    private final Supplier<String> objectIdGenerator;

    public UploadFileService(FileStoragePort fileStoragePort) {
        this(fileStoragePort, () -> UUID.randomUUID().toString());
    }

    public UploadFileService(FileStoragePort fileStoragePort, Supplier<String> objectIdGenerator) {
        this.fileStoragePort = fileStoragePort;
        this.objectIdGenerator = objectIdGenerator;
    }

    @Override
    public UploadFileResult upload(UploadFileCommand command) {
        validate(command);

        String contentType = normalizeContentType(command.getContentType());
        String objectName = buildObjectName(command.getOriginalFilename());
        StoredFileLocation location = fileStoragePort.store(new FileStorageObject(
                objectName,
                contentType,
                command.getSize(),
                command.getContent()
        ));

        return new UploadFileResult(
                location.getBucket(),
                location.getObjectName(),
                command.getOriginalFilename(),
                contentType,
                command.getSize()
        );
    }

    private void validate(UploadFileCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Upload file command is required.");
        }
        if (command.getContent() == null) {
            throw new IllegalArgumentException("File content is required.");
        }
        if (command.getSize() <= 0) {
            throw new IllegalArgumentException("File must not be empty.");
        }
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return DEFAULT_CONTENT_TYPE;
        }
        return contentType;
    }

    private String buildObjectName(String originalFilename) {
        String safeFilename = sanitizeFilename(originalFilename);
        return objectIdGenerator.get() + "-" + safeFilename;
    }

    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return "file";
        }
        String onlyFilename = originalFilename.replace("\\", "/");
        int lastSlashIndex = onlyFilename.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            onlyFilename = onlyFilename.substring(lastSlashIndex + 1);
        }
        String sanitized = onlyFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (sanitized.isEmpty()) {
            return "file";
        }
        return sanitized;
    }
}
