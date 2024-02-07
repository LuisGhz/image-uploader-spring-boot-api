package com.imageuploader.app.services;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Value("${aws.bucketName}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Override
    public String uploadImage(MultipartFile image) {
        File file = new File(image.getOriginalFilename());

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(image.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileName = generateFileName(image);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(file.length());
        putObjectRequest.setMetadata(metadata);
        putObjectRequest.setMetadata(metadata);
        s3Client.putObject(putObjectRequest);

        file.delete();

        return fileName;
    }

    @SuppressWarnings("null")
    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }
}
