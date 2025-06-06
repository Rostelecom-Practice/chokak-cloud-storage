package com.practice.review.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;

class FileSystemStorageServiceTest {

	@TempDir
	Path tempDir;

	FileSystemStorageService storageService;

	@BeforeEach
	void setUp() throws IOException {
		String storageLocation = tempDir.toString();
		storageService = new FileSystemStorageService(storageLocation);
	}

	@Test
	void testSave() throws IOException {
		String originalFilename = "test.txt";
		byte[] content = "Save file test".getBytes();
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file", originalFilename, "text/plain", content
		);

		String savedFilename = storageService.save(multipartFile);

		assertThat(savedFilename).contains(originalFilename);
		Path savedFilePath = tempDir.resolve(savedFilename);
		assertThat(Files.exists(savedFilePath)).isTrue();
		assertThat(Files.readAllBytes(savedFilePath)).isEqualTo(content);
	}

	@Test
	void testLoad() throws IOException {
		String filename = "myfile.txt";
		byte[] data = "data".getBytes();
		Path filePath = tempDir.resolve(filename);
		Files.write(filePath, data);

		Resource resource = storageService.load(filename);

		assertThat(resource).isNotNull();
		assertThat(resource.exists()).isTrue();
		try (InputStream in = resource.getInputStream()) {
			assertThat(in.readAllBytes()).isEqualTo(data);
		}
	}

	@Test
	void testDelete() throws IOException {
		String filename = "deleteFile.txt";
		Path filePath = tempDir.resolve(filename);
		Files.createFile(filePath);

		storageService.delete(filename);

		assertThat(Files.exists(filePath)).isFalse();
	}

	@Test
	void testLoad_NotFound() throws MalformedURLException, FileNotFoundException {
		String notExist = "not_found.txt";

		Resource resource = storageService.load(notExist);

		assertThat(resource.exists()).isFalse();
	}
}
