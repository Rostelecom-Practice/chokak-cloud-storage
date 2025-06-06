package com.practice.review.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
    public Resource load(String filename) throws FileNotFoundException {
        File file = root.resolve(filename).toFile();

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return new FileSystemResource(file);
    }

    @Override
    public void delete(String filename) throws IOException {
        Files.deleteIfExists(root.resolve(filename));
    }
}
