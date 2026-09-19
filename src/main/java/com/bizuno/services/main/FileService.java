package com.bizuno.services.main;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.main.CommonResponse;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {

    public String saveFile(MultipartFile file, String subFolder) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // uploads folder beside jar
            Path basePath = getUploadBasePath();

            // create uploads folder if not exists
            Files.createDirectories(basePath);

            // create subfolder (example: users, documents, aadhaar)
            Path subFolderPath = basePath.resolve(subFolder).normalize();
            Files.createDirectories(subFolderPath);

            // create safe filename
            String originalName = Objects.requireNonNull(file.getOriginalFilename());

            // Normalize unicode characters
            originalName = Normalizer.normalize(originalName, Normalizer.Form.NFKC);

            // Remove unsafe characters
            originalName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");

            // Remove duplicate underscores
            originalName = originalName.replaceAll("_+", "_");

            // Optional: trim underscores from start/end
            originalName = originalName.replaceAll("^_+|_+$", "");

            String fileName = UUID.randomUUID() + "_" + originalName;

            // final file path
            Path filePath = subFolderPath.resolve(fileName);

            // save file
            file.transferTo(filePath.toFile());

            return "uploads/" + basePath.relativize(filePath).toString().replace("\\", "/");

        } catch (IOException e) {
            throw new ServiceException("Error saving file: " + e.getMessage());
        }
    }

    public CommonResponse deleteFile(String relativePath) {

        if (relativePath == null || relativePath.isBlank()) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "File path is empty", null);
        }

        try {

            // Prevent path traversal attack
            if (relativePath.contains("..")) {
                return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "Invalid file path", null);
            }

            // remove uploads prefix if present
            if (relativePath.startsWith("uploads/")) {
                relativePath = relativePath.substring(8);
            }

            // uploads base directory
            Path basePath = getUploadBasePath();

            // resolve full file path
            Path filePath = basePath.resolve(relativePath).normalize();

            if (!Files.exists(filePath)) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, "File not found", null);
            }

            Files.delete(filePath);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, "File deleted successfully", null);

        } catch (Exception e) {
            return new CommonResponse(AppConstants.STATUS_INTERNAL_SERVER_ERROR, "Error deleting file: " + e.getMessage(), null);
        }
    }

    public String toFileUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return null;

        try {
            if (relativePath.startsWith("uploads/")) {
                relativePath = relativePath.substring(8);
            }

            Path filePath = getUploadBasePath()
                    .resolve(relativePath)
                    .normalize()
                    .toAbsolutePath();

            return filePath.toUri().toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve file path: " + relativePath, e);
        }
    }

    public Path resolveResourcePath(String relativePath) {
        return getResourcesBasePath()
                .resolve(relativePath)
                .normalize()
                .toAbsolutePath();
    }

    private Path getUploadBasePath() {
        String rootPath = System.getProperty("user.dir");
        return Paths.get(rootPath, "files", "uploads");
    }

    public Path getResourcesBasePath() {
        String rootPath = System.getProperty("user.dir");
        return Paths.get(rootPath, "files", "resources");
    }

}
