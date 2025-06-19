package com.coachera.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Video;
import com.coachera.backend.service.VideoService;
import com.coachera.backend.service.VideoViewingService;

@RestController
@RequestMapping("/api")
public class VideoController {
    private final VideoService videoService;
    private final VideoViewingService videoViewingService;

    @Autowired
    public VideoController(VideoService videoService, VideoViewingService videoViewingService) {
        this.videoViewingService = videoViewingService;
        this.videoService = videoService;
    }

    @PostMapping(value = "/video/upload")
    public ApiResponse<String> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        Video video = videoService.store(file);
        return ApiResponse.created("Video was uploaded successfully", video.getUrl());
    }

    @GetMapping("/video/{videoId}")
    public ApiResponse<Video> getVideo(@PathVariable Long videoId) {
        Video video = videoService.getVideoById(videoId);
        return ApiResponse.success(video);
    }

    @DeleteMapping("/video/{videoId}")
    public ApiResponse<Void> deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return ApiResponse.noContentResponse();
    }

    @PostMapping("/progress")
    public void recordProgress(
            @AuthenticationPrincipal User user,
            @RequestParam Integer materialId,
            @RequestParam double percentWatched) {
            
        videoViewingService.recordViewing(user, materialId, percentWatched);
    }
}