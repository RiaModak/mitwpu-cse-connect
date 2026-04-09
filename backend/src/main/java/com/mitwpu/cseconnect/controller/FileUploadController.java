package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.response.ApiResponse;
import com.mitwpu.cseconnect.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subfolder", defaultValue = "general") String subfolder) {
        String url = fileStorageService.store(file, subfolder);
        return ResponseEntity.ok(ApiResponse.success("File uploaded", Map.of("url", url)));
    }
}
