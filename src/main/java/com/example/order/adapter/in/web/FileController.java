package com.example.order.adapter.in.web;

import com.example.order.application.port.in.UploadFileCommand;
import com.example.order.application.port.in.UploadFileResult;
import com.example.order.application.port.in.UploadFileUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/files")
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;

    public FileController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping
    public ResponseEntity<UploadFileResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        UploadFileResult result = uploadFileUseCase.upload(new UploadFileCommand(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        ));

        UploadFileResponse response = new UploadFileResponse(
                result.getBucket(),
                result.getObjectName(),
                result.getOriginalFilename(),
                result.getContentType(),
                result.getSize()
        );
        return ResponseEntity.created(URI.create("/files/" + result.getObjectName())).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleBadRequest() {
        return ResponseEntity.badRequest().build();
    }
}
