package com.example.order.application.service;

import com.example.order.application.port.in.UploadFileCommand;
import com.example.order.application.port.in.UploadFileResult;
import com.example.order.application.port.out.FileStorageObject;
import com.example.order.application.port.out.FileStoragePort;
import com.example.order.application.port.out.StoredFileLocation;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UploadFileServiceTest {

    @Test
    void uploadStoresFileThroughStoragePort() {
        AtomicReference<FileStorageObject> storedObject = new AtomicReference<>();
        FileStoragePort storagePort = file -> {
            storedObject.set(file);
            return new StoredFileLocation("order-files", file.getObjectName());
        };
        UploadFileService service = new UploadFileService(storagePort, () -> "file-id");
        InputStream content = new ByteArrayInputStream("hello".getBytes());

        UploadFileResult result = service.upload(new UploadFileCommand(
                "invoice 01.pdf",
                "application/pdf",
                5,
                content
        ));

        assertEquals("order-files", result.getBucket());
        assertEquals("file-id-invoice_01.pdf", result.getObjectName());
        assertEquals("invoice 01.pdf", result.getOriginalFilename());
        assertEquals("application/pdf", result.getContentType());
        assertEquals(5, result.getSize());
        assertEquals("file-id-invoice_01.pdf", storedObject.get().getObjectName());
        assertEquals("application/pdf", storedObject.get().getContentType());
        assertEquals(5, storedObject.get().getSize());
    }

    @Test
    void uploadRejectsEmptyFile() {
        UploadFileService service = new UploadFileService(file -> new StoredFileLocation("bucket", "object"));

        assertThrows(IllegalArgumentException.class, () -> service.upload(new UploadFileCommand(
                "empty.txt",
                "text/plain",
                0,
                new ByteArrayInputStream(new byte[0])
        )));
    }

    @Test
    void uploadDefaultsMissingContentType() {
        UploadFileService service = new UploadFileService(
                file -> new StoredFileLocation("order-files", file.getObjectName()),
                () -> "file-id"
        );

        UploadFileResult result = service.upload(new UploadFileCommand(
                "readme",
                null,
                4,
                new ByteArrayInputStream("test".getBytes())
        ));

        assertEquals("application/octet-stream", result.getContentType());
    }
}
