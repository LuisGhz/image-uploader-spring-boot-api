package com.imageuploader.app.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolationException;

import com.imageuploader.app.annotations.Image;
import com.imageuploader.app.services.ImageService;
import com.imageuploader.app.web.APIResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/image")
@Validated
@RequiredArgsConstructor
public class ImageUploaderController {
    private final ImageService imageService;

    @PostMapping()
    public ResponseEntity<?> uploadImage(
            @Image @RequestParam(name = "image", required = false) MultipartFile image) {
        try {
            String fileName = this.imageService.uploadImage(image);
            APIResponse response = APIResponse.builder().message("Image uploaded!").isSuccessful(true)
                    .httpStatus(HttpStatus.OK).data(fileName).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error uploading image", e);
            APIResponse response = APIResponse.builder().message("Error uploading image").isSuccessful(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).data(null).build();

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleConstraintViolationException(ConstraintViolationException e) {
        log.info(e.getLocalizedMessage());
        return "Validation error: " + e.getMessage();
    }
}
