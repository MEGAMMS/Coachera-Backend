package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.FavoriteDTO;
import com.coachera.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints for managing student's favorite courses")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all favorites for a student")
    public ApiResponse<?> getFavoritesByStudentId(@PathVariable Integer studentId) {

        List<FavoriteDTO> favorites = favoriteService.getFavoritesByStudentId(studentId);
        return ApiResponse.success(favorites);

    }

    @PostMapping("{courseId}/student/{studentId}")
    @Operation(summary = "Add a course to student's favorites")
    public ApiResponse<?> addFavorite(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId) {

        FavoriteDTO favoriteDTO = favoriteService.addFavorite(studentId, courseId);
        return ApiResponse.created("Student added course to favorite list", favoriteDTO);

    }

    @DeleteMapping("delete/{courseId}/{studentId}")
    @Operation(summary = "Remove a course from student's favorites")
    public ApiResponse<?> removeFavorite(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId) {

        favoriteService.removeFavorite(studentId, courseId);
        return ApiResponse.noContent();

    }

    @GetMapping("/check/{studentId}/{courseId}")
    @Operation(summary = "Check if a course is favorited by a student")
    public ApiResponse<?> isCourseFavorited(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId) {

        boolean isFavorited = favoriteService.isCourseFavoritedByStudent(studentId, courseId);
        return ApiResponse.success(isFavorited);

    }
}