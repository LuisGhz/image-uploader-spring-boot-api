package com.imageuploader.app.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import jakarta.validation.ConstraintViolationException;

import com.imageuploader.app.annotations.Image;
import com.imageuploader.app.services.ImageService;
import com.imageuploader.app.web.APIResponse;

@RestController
@RequestMapping("/api/v1/image")
@Validated
@RequiredArgsConstructor
public class ImageUploaderController {
    private final Logger log = LoggerFactory.getLogger(ImageUploaderController.class);
    private final ImageService imageService;

    @Value("${aws.publicUrl}")
    private String publicUrl;

    @PostMapping()
    public ResponseEntity<?> uploadImage(
            @Image @RequestParam(name = "image", required = false) MultipartFile image) {
        try {
            String fileName = this.imageService.uploadImage(image);
            final String fileUrl = publicUrl + fileName;
            this.LogImageUploaded(image.getOriginalFilename(), fileName);
            APIResponse response = APIResponse.builder().message("Image uploaded!").isSuccessful(true)
                    .httpStatus(HttpStatus.OK).data(fileUrl).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error uploading image", e);
            APIResponse response = APIResponse.builder().message("Error uploading image").isSuccessful(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).data(null).build();

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void LogImageUploaded(String originalName, String newName) {
        Map<String, String> logData = new HashMap<>();
        logData.put("originalName", originalName);
        logData.put("newName", newName);
        log.info("Image uploaded {}", logData);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        APIResponse response = APIResponse.builder().message("Validation error").isSuccessful(false)
                .httpStatus(HttpStatus.BAD_REQUEST).data(e.getMessage()).build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
