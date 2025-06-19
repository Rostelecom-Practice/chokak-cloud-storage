package com.practice.review.service;

import com.practice.review.entity.ImageMetadata;
import com.practice.review.repository.ImageMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {
    private final Path root;
    private final ImageMetadataRepository metadataRepository;

    public FileSystemStorageService(
            @Value("${storage.location}") String storagePath,
            ImageMetadataRepository metadataRepository
    ) throws IOException {
        this.root = Paths.get(storagePath);
        Files.createDirectories(root);
        this.metadataRepository = metadataRepository;
    }

    @Override
    public String save(MultipartFile file, String ownerUid) throws IOException {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path destination = root.resolve(filename);
        Files.copy(file.getInputStream(), destination);

        metadataRepository.save(new ImageMetadata(filename, ownerUid));
        return filename;
    }

    @Override
    public Resource load(String filename) throws FileNotFoundException {
        File file = root.resolve(filename).toFile();
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден");
        }
        return new FileSystemResource(file);
    }

    @Override
    public void delete(String filename, String requesterUid) throws IOException {
        ImageMetadata metadata = metadataRepository.findById(filename)
                .orElseThrow(() -> new FileNotFoundException("Метаданные не найдены"));

        if (!metadata.getOwnerUid().equals(requesterUid)) {
            throw new SecurityException("Вы не являетесь владельцем файла");
        }

        Files.deleteIfExists(root.resolve(filename));
        metadataRepository.deleteById(filename);
    }
}
