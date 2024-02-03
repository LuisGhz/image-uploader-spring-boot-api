package com.imageuploader.app.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import com.imageuploader.app.annotations.Image;

@Slf4j
@RestController
@RequestMapping("/api/v1/image")
@Validated
public class ImageUploaderController {
    @PostMapping()
    public String uploadImage(@Image @NotNull @RequestParam(name = "image", required = false) MultipartFile image) {
        log.info("Image receive", image.getOriginalFilename());
        return image.getOriginalFilename();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleConstraintViolationException(ConstraintViolationException e) {
        log.info(e.getLocalizedMessage());
        return "Validation error: " + e.getMessage();
    }
}
