package com.practice.review.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.practice.review.service.StorageService;

@RestController
@RequestMapping("/images")
public class FileController {
    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/images")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.getSize()>50 * 1024 * 1024){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Max file size — 50MB"));
        }
        String filename = storageService.save(file);
        String url = "/images/" + filename;
        return ResponseEntity.created(URI.create(url)).body(Map.of("url", url));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Resource resource = storageService.load(filename);

            String ext = StringUtils.getFilenameExtension(filename);
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

            if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if ("png".equalsIgnoreCase(ext)) {
                mediaType = MediaType.IMAGE_PNG;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        } catch (RuntimeException | MalformedURLException | FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<Void> delete(@PathVariable String filename) throws IOException {
        storageService.delete(filename);
        return ResponseEntity.noContent().build();
    }
}
