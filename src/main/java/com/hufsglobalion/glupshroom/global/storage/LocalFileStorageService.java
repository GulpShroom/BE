package com.hufsglobalion.glupshroom.global.storage;

import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final Path rootDir;
    private final String baseUrl;

    public LocalFileStorageService(
            @Value("${app.upload.dir}") String uploadDir,
            @Value("${app.upload.base-url}") String baseUrl
    ) {
        this.rootDir = Path.of(uploadDir);
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty() || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_INVALID);
        }

        String datePath = LocalDate.now().toString().replace("-", "/");
        String filename = UUID.randomUUID() + extensionOf(file.getContentType());

        try {
            Path targetDir = rootDir.resolve(datePath);
            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(filename);
            file.transferTo(targetFile);

            return baseUrl + "/" + datePath + "/" + filename;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private String extensionOf(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
