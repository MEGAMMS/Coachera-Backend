package com.coachera.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.entity.Image;
import com.coachera.backend.service.ImageService;

@RestController
@RequestMapping("/api")
public class ImageController {
    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/image/upload")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        Image image = imageService.store(file);
        return ApiResponse.created("Image was uploaded successfully", image.getUrl());
    }
}
