package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        // Set the upload directory to our temp directory for testing
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    @Test
    void shouldStoreFileSuccessfully() throws IOException {
        // Arrange
        String content = "Test file content";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes()
        );

        // Act
        String storedPath = fileStorageService.storeFile(file, "proofs");

        // Assert
        assertThat(storedPath).startsWith("proofs/");
        assertThat(storedPath).endsWith(".txt");
        
        // Verify file was actually stored
        Path fullPath = fileStorageService.getFilePath(storedPath);
        assertThat(Files.exists(fullPath)).isTrue();
        
        // Verify content
        String actualContent = Files.readString(fullPath);
        assertThat(actualContent).isEqualTo(content);
    }

    @Test
    void shouldCreateDirectoryIfNotExists() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        // Act
        String storedPath = fileStorageService.storeFile(file, "new-subdirectory");

        // Assert
        assertThat(storedPath).startsWith("new-subdirectory/");
        Path subDir = tempDir.resolve("new-subdirectory");
        assertThat(Files.exists(subDir)).isTrue();
        assertThat(Files.isDirectory(subDir)).isTrue();
    }

    @Test
    void shouldGenerateUniqueFilenames() {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content2".getBytes()
        );

        // Act
        String path1 = fileStorageService.storeFile(file1, "proofs");
        String path2 = fileStorageService.storeFile(file2, "proofs");

        // Assert
        assertThat(path1).isNotEqualTo(path2);
        assertThat(path1).endsWith(".txt");
        assertThat(path2).endsWith(".txt");
        
        // Verify both files exist
        assertThat(Files.exists(fileStorageService.getFilePath(path1))).isTrue();
        assertThat(Files.exists(fileStorageService.getFilePath(path2))).isTrue();
    }

    @Test
    void shouldPreserveFileExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "png content".getBytes()
        );

        // Act
        String storedPath = fileStorageService.storeFile(file, "images");

        // Assert
        assertThat(storedPath).endsWith(".png");
    }

    @Test
    void shouldHandleFileWithoutExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "noextension",
                "application/octet-stream",
                "binary content".getBytes()
        );

        // Act
        String storedPath = fileStorageService.storeFile(file, "files");

        // Assert
        assertThat(storedPath).startsWith("files/");
        assertThat(storedPath).doesNotContain(".");
    }

    @Test
    void shouldThrowExceptionForEmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        // Act & Assert
        assertThatThrownBy(() -> fileStorageService.storeFile(emptyFile, "proofs"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Cannot store empty file");
    }

    @Test
    void shouldGetCorrectFilePath() {
        // Arrange
        String relativePath = "proofs/test-file.jpg";

        // Act
        Path filePath = fileStorageService.getFilePath(relativePath);

        // Assert
        assertThat(filePath).isEqualTo(tempDir.resolve(relativePath));
    }

    @Test
    void shouldReplaceExistingFile() throws IOException {
        // Arrange
        String originalContent = "original content";
        String newContent = "new content";
        
        MockMultipartFile originalFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                originalContent.getBytes()
        );

        // First store
        String storedPath = fileStorageService.storeFile(originalFile, "proofs");
        Path fullPath = fileStorageService.getFilePath(storedPath);
        
        // Verify original content
        assertThat(Files.readString(fullPath)).isEqualTo(originalContent);

        // Create new file with same name (but different UUID will be generated)
        MockMultipartFile newFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                newContent.getBytes()
        );

        // Act - store new file
        String newStoredPath = fileStorageService.storeFile(newFile, "proofs");

        // Assert
        assertThat(newStoredPath).isNotEqualTo(storedPath); // Different UUID
        
        // Both files should exist with their respective content
        assertThat(Files.readString(fullPath)).isEqualTo(originalContent);
        assertThat(Files.readString(fileStorageService.getFilePath(newStoredPath))).isEqualTo(newContent);
    }

    @Test
    void shouldHandleIOException() {
        // Arrange - Create a mock file that will cause IOException
        MultipartFile problematicFile = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes()) {
            @Override
            public java.io.InputStream getInputStream() throws IOException {
                throw new IOException("Simulated IO error");
            }
        };

        // Act & Assert
        assertThatThrownBy(() -> fileStorageService.storeFile(problematicFile, "proofs"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Failed to store file: Simulated IO error");
    }
}
