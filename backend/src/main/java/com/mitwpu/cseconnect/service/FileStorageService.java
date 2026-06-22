package com.mitwpu.cseconnect.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file, String subfolder);
}
