package com.imageuploader.app.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private ImageServiceImpl imageService;

    @Mock
    private AmazonS3 s3Client;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageServiceImpl(s3Client);
    }

    @Test
    public void testUploadImage() throws IOException {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg",
                "test data".getBytes());

        when(s3Client.putObject(any())).thenReturn(null);

        String fileName = imageService.uploadImage(image);

        Assertions.assertThat(fileName).isNotNull();
        Assertions.assertThat(fileName).contains("test.jpg"); 
        Mockito.verify(s3Client, times(1)).putObject(any());
    }

    @Test
    public void testUploadImageWithException() throws IOException {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg",
                "test data".getBytes());

        when(s3Client.putObject(any())).thenThrow(new RuntimeException("Error uploading image"));

        Assertions.assertThatThrownBy(() -> {
            imageService.uploadImage(image);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Error uploading image");
    }

}