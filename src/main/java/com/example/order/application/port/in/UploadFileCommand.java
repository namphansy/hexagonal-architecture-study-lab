package com.example.order.application.port.in;

import java.io.InputStream;

public class UploadFileCommand {

    private final String originalFilename;
    private final String contentType;
    private final long size;
    private final InputStream content;

    public UploadFileCommand(String originalFilename, String contentType, long size, InputStream content) {
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
        this.content = content;
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

    public InputStream getContent() {
        return content;
    }
}
