package com.practice.review.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {
    private final Path root;

    public FileSystemStorageService(@Value("${storage.location}") String storagePath) throws IOException {
        this.root = Paths.get(storagePath);
        Files.createDirectories(root);
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), root.resolve(filename));
        return filename;
    }

    @Override
    public Resource load(String filename) {
        try {
            Path filePath = root.resolve(filename);
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }

    @Override
    public void delete(String filename) throws IOException {
        Files.deleteIfExists(root.resolve(filename));
    }
}
