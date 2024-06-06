package com.pmj.s3.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Log4j2
public class S3Service {

    private AmazonS3 s3client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String uploadFile(String keyName, MultipartFile file) throws IOException {
        s3client.putObject(bucketName, keyName, file.getInputStream(), null);
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, Regions.US_EAST_1.getName(), keyName);
        log.info("File uploaded to : " + fileUrl);
        return fileUrl;
    }
    public S3Object getFile(String keyName) {
        return s3client.getObject(bucketName, keyName);
    }
}