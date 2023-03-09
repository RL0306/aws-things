package com.example.testaws.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
public class AwsService {

    private AWSCredentials credentials;
    private AmazonS3 s3client;
    @Value("${aws.bucketname}")
    private String bucketName;

    public AwsService(@Value("${aws.accesskey}") String access_key, @Value("${aws.secretkey}") String secret_key){
        this.credentials = new BasicAWSCredentials(access_key, secret_key);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public void uploadFileToBucket(String fileName, MultipartFile multipartFile){

        File convertedFile = convertMultipartToFile(multipartFile);
        s3client.putObject(
                bucketName,
                fileName,
                convertedFile
        );
    }

    public File getFileFromBucket(String fileName) throws IOException {
        S3Object s3object = s3client.getObject(bucketName, fileName);
        if(s3object == null)throw new IllegalArgumentException("File does not exist");

        S3ObjectInputStream inputStream = s3object.getObjectContent();
        File file = new File(fileName);

        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return file;
    }

    public File convertMultipartToFile(MultipartFile multipartFile){
        try {
            File convertedFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            FileOutputStream fos = new FileOutputStream(convertedFile);
            fos.write(multipartFile.getBytes());
            fos.close();
            return convertedFile;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
