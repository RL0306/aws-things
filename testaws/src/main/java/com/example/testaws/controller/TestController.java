package com.example.testaws.controller;

import com.example.testaws.dto.FileRequestDTO;
import com.example.testaws.service.AwsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/test")
@AllArgsConstructor
@Slf4j
public class TestController {

    private final AwsService awsService;

    @GetMapping
    public Map<String, String> test(){

        return Map.of("request", "success");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file) throws IOException {
        awsService.uploadFileToBucket(
                file.getOriginalFilename(),
                file
        );
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/retrieve")
    public ResponseEntity<FileSystemResource> getFile(@RequestBody FileRequestDTO fileRequestDTO) throws IOException {
        File fileFromBucket = awsService.getFileFromBucket(fileRequestDTO.fileName());
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.IMAGE_PNG);
        respHeaders.setContentDispositionFormData("attachment", fileFromBucket.getName());
        return new ResponseEntity<>(
                new FileSystemResource(fileFromBucket), respHeaders, HttpStatus.OK
        );
    }



}
