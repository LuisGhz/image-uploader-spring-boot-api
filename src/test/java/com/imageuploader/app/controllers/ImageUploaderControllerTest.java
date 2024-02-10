package com.imageuploader.app.controllers;

import com.imageuploader.app.services.ImageService;
import com.imageuploader.app.web.controllers.ImageUploaderController;

import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ImageUploaderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @InjectMocks
    private ImageUploaderController imageUploaderController;

    @Test
    public void uploadImage() throws Exception {
        // Create a multipart file
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test data".getBytes());

        when(imageService.uploadImage(image)).thenReturn("Filename");

        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/api/v1/image").file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void invalidImageShouldntBeUploaded() throws Exception {
        // Create a multipart file
        MockMultipartFile image = new MockMultipartFile("image", "test.txt", "plain/text", "test data".getBytes());
        byte[] largeContent = new byte[1024 * 1024 * 3];
        Arrays.fill(largeContent, (byte) 65);
        MockMultipartFile largeImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", largeContent);
        MockMultipartFile emptyImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);

        when(imageService.uploadImage(image)).thenReturn("Filename");

        ResultActions typeResult = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/api/v1/image").file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));
        ResultActions nullResult = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/api/v1/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));
        ResultActions largeResult = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/api/v1/image").file(largeImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));
        ResultActions emptyResult = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/api/v1/image").file(emptyImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        typeResult.andExpect(MockMvcResultMatchers.status().isBadRequest());
        nullResult.andExpect(MockMvcResultMatchers.status().isBadRequest());
        largeResult.andExpect(MockMvcResultMatchers.status().isBadRequest());
        emptyResult.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}