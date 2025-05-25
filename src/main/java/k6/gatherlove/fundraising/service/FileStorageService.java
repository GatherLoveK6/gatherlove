package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    public String storeFile(MultipartFile file, String subDirectory) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new ValidationException("Cannot store empty file");
            }
            
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, subDirectory);
            Files.createDirectories(uploadPath);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + fileExtension;
            
            // Store file
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File stored successfully: {}", targetLocation);
            
            // Return relative path for database storage
            return subDirectory + "/" + filename;
            
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new ValidationException("Failed to store file: " + e.getMessage());
        }
    }
    
    public Path getFilePath(String relativePath) {
        return Paths.get(uploadDir).resolve(relativePath);
    }
}
