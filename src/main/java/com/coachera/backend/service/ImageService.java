package com.coachera.backend.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import com.coachera.backend.entity.Image;
import com.coachera.backend.repository.ImageRepository;

@Service
public class ImageService {

    private final Path rootLocation;
    private ImageRepository imageRepository;

    public boolean validateImageUuid(String uuidName) {
        return imageRepository.existsByUuidName(uuidName);
    }

    public Image getImageFromUrl(String imageUrl) {
        String uuidName = Image.extractUuidFromUrl(imageUrl);
        return imageRepository.findByUuidName(uuidName)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));
    }

    @Autowired
    public ImageService(@Value("${app.upload.dir}") String uploadDir,
            ImageRepository imageRepository) throws IOException {
        this.rootLocation = Paths.get(uploadDir);
        this.imageRepository = imageRepository;
        Files.createDirectories(rootLocation);
    }

    public Image store(MultipartFile file) throws IOException {
        String originalExtension = getFileExtension(file.getOriginalFilename());
        String uuidName = UUID.randomUUID() + originalExtension;

        Files.copy(file.getInputStream(), this.rootLocation.resolve(uuidName),
                StandardCopyOption.REPLACE_EXISTING);

        Image image = new Image();
        image.setUuidName(uuidName);
        imageRepository.save(image);

        return image;
    }

    private String getFileExtension(String fileName) {
        return fileName != null ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }
}
