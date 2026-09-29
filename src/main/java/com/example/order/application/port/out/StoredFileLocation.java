package com.example.order.application.port.out;

public class StoredFileLocation {

    private final String bucket;
    private final String objectName;

    public StoredFileLocation(String bucket, String objectName) {
        this.bucket = bucket;
        this.objectName = objectName;
    }

    public String getBucket() {
        return bucket;
    }

    public String getObjectName() {
        return objectName;
    }
}
