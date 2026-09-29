package com.example.order.application.port.in;

public class UploadFileResult {

    private final String bucket;
    private final String objectName;
    private final String originalFilename;
    private final String contentType;
    private final long size;

    public UploadFileResult(String bucket, String objectName, String originalFilename, String contentType, long size) {
        this.bucket = bucket;
        this.objectName = objectName;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
    }

    public String getBucket() {
        return bucket;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }
}
