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

import com.coachera.backend.entity.Video;
import com.coachera.backend.repository.VideoRepository;
import com.coachera.backend.exception.ResourceNotFoundException;

@Service
public class VideoService {

    private final Path rootLocation;
    private VideoRepository videoRepository;

    public boolean validateVideoUuid(String uuidName) {
        return videoRepository.existsByUuidName(uuidName);
    }

    public Video getVideoFromUrl(String videoUrl) {
        String uuidName = Video.extractUuidFromUrl(videoUrl);
        return videoRepository.findByUuidName(uuidName)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));
    }

    public Video createVideoFromUrl(String videoUrl) {
        Video video = new Video();
        video.setExternalUrl(videoUrl);
        video.setUuidName(UUID.randomUUID().toString()); // Still need a unique identifier
        videoRepository.save(video);
        
        return video;
    }

    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));
    }

    public void deleteVideo(Long videoId) {
        Video video = getVideoById(videoId);
        
        // Delete the physical file
        try {
            Path videoPath = rootLocation.resolve(video.getUuidName());
            Files.deleteIfExists(videoPath);
        } catch (IOException e) {
            // Log the error but don't fail the operation
            System.err.println("Failed to delete video file: " + e.getMessage());
        }
        
        // Delete from database
        videoRepository.delete(video);
    }

    @Autowired
    public VideoService(@Value("${app.upload.dir}") String uploadDir,
            VideoRepository videoRepository) throws IOException {
        this.rootLocation = Paths.get(uploadDir);
        this.videoRepository = videoRepository;
        Files.createDirectories(rootLocation);
    }

    public Video store(MultipartFile file) throws IOException {
        String originalExtension = getFileExtension(file.getOriginalFilename());
        String uuidName = UUID.randomUUID() + originalExtension;

        Files.copy(file.getInputStream(), this.rootLocation.resolve(uuidName),
                StandardCopyOption.REPLACE_EXISTING);

        Video video = new Video();
        video.setUuidName(uuidName);
        videoRepository.save(video);

        return video;
    }

    private String getFileExtension(String fileName) {
        return fileName != null ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }
} 