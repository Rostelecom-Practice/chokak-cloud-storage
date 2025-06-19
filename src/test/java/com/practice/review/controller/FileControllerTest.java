package com.practice.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.practice.review.service.StorageService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageService storageService;

    @Test
    void testUpload_Success() throws Exception {
        byte[] content = "test image content".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "original.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                content
        );

        when(storageService.save(any(MockMultipartFile.class), any(String.class)))
                .thenReturn("saved.jpg");


        mockMvc.perform(multipart("/images/images")
                        .file(mockFile)
                        .header("X-User-Uid", "test-uid"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").value("/images/saved.jpg"));
    }

    @Test
    void testGetImage_Success() throws Exception {
        String filename = "example.jpg";
        byte[] fakeImageData = "fake image bytes".getBytes(StandardCharsets.UTF_8);

        Resource fakeResource = new InputStreamResource(new ByteArrayInputStream(fakeImageData)) {
            @Override
            public String getFilename() {
                return filename;
            }
            @Override
            public long contentLength() {
                return fakeImageData.length;
            }
        };

        when(storageService.load(eq(filename))).thenReturn(fakeResource);

        mockMvc.perform(get("/images/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(fakeImageData));
    }

    @Test
    void testDelete_Success() throws Exception {
        String filename = "to-delete.jpg";
        doNothing().when(storageService).delete(eq(filename), any(String.class));

        mockMvc.perform(delete("/images/{filename}", filename)
                        .header("X-User-Uid", "test-uid"))
                .andExpect(status().isNoContent());
    }

}
