package com.example.order.application.port.out;

import java.io.InputStream;

public class FileStorageObject {

    private final String objectName;
    private final String contentType;
    private final long size;
    private final InputStream content;

    public FileStorageObject(String objectName, String contentType, long size, InputStream content) {
        this.objectName = objectName;
        this.contentType = contentType;
        this.size = size;
        this.content = content;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public InputStream getContent() {
        return content;
    }
}
