package com.practice.review.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String save(MultipartFile file) throws IOException;
    Resource load(String filename);
    void delete(String filename) throws IOException;
}
