package com.hufsglobalion.glupshroom.global.storage;

import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File", description = "파일 업로드 API")
@RestController
@RequestMapping("/api/v1/mcarry/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "이미지 업로드", description = "이미지 파일을 업로드하고 접근 가능한 URL을 반환합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.store(file);
        return ApiResponse.of("S201", "파일 업로드에 성공했습니다", new FileUploadResponse(url));
    }
}
