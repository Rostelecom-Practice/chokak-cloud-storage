package com.practice.review.service;

import com.practice.review.entity.ImageMetadata;
import com.practice.review.repository.ImageMetadataRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class FileSystemStorageServiceTest {

	@TempDir
	Path tempDir;

	FileSystemStorageService storageService;
	ImageMetadataRepository metadataRepository;

	@BeforeEach
	void setUp() throws IOException {
		String storageLocation = tempDir.toString();
		metadataRepository = Mockito.mock(ImageMetadataRepository.class);
		storageService = new FileSystemStorageService(storageLocation, metadataRepository);
	}

	@Test
	void testSave() throws IOException {
		String originalFilename = "test.txt";
		byte[] content = "Save file test".getBytes();
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file", originalFilename, "text/plain", content
		);

		Mockito.when(metadataRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		String savedFilename = storageService.save(multipartFile, "test-owner-uid");

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

		Mockito.when(metadataRepository.findById(filename))
				.thenReturn(java.util.Optional.of(new ImageMetadata(filename, "test-owner-uid")));


		Mockito.doNothing().when(metadataRepository).deleteById(filename);
		storageService.delete(filename, "test-owner-uid");

		assertThat(Files.exists(filePath)).isFalse();
	}

	@Test
	void testLoad_NotFound()  {
		String notExist = "not_found.txt";

		assertThrows(FileNotFoundException.class, () -> storageService.load(notExist));
	}
}
