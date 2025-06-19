package com.practice.review.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String save(MultipartFile file, String ownerUid) throws IOException;
    Resource load(String filename) throws MalformedURLException, FileNotFoundException;
    void delete(String filename, String requesterUid) throws IOException;
}
