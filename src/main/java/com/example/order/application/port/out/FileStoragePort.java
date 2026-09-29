package com.example.order.application.port.out;

public interface FileStoragePort {

    StoredFileLocation store(FileStorageObject file);
}
