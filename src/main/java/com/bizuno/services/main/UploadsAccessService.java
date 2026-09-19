package com.bizuno.services.main;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadsAccessService {

    public Resource getFile(String relativePath) {
        if (relativePath.contains("..")) {
            return null;
        }

        Path basePath = getUploadBasePath();
        Path filePath = basePath.resolve(relativePath).normalize().toAbsolutePath();

        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            return null;
        }

        return resource;
    }

    private Path getUploadBasePath() {
        String rootPath = System.getProperty("user.dir");
        return Paths.get(rootPath, "files", "uploads");
    }
}

