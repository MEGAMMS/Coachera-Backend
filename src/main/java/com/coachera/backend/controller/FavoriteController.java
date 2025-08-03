package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.FavoriteDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints for managing student's favorite courses")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/student")
    @Operation(summary = "Get all favorites for a student")
    public ApiResponse<?> getFavoritesByStudentId(@AuthenticationPrincipal User user) {

        List<FavoriteDTO> favorites = favoriteService.getFavoritesByStudentId(user);
        return ApiResponse.success(favorites);

    }

    @PostMapping("{courseId}/student")
    @Operation(summary = "Add a course to student's favorites")
    public ApiResponse<?> addFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Integer courseId) {

        FavoriteDTO favoriteDTO = favoriteService.addFavorite(user, courseId);
        return ApiResponse.created("Student added course to favorite list", favoriteDTO);

    }

    @DeleteMapping("{courseId}")
    @Operation(summary = "Remove a course from student's favorites")
    public ApiResponse<?> removeFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Integer courseId) {

        favoriteService.removeFavorite(user, courseId);
        return ApiResponse.noContentResponse();

    }

    @GetMapping("/check/{courseId}")
    @Operation(summary = "Check if a course is favorited by a student")
    public ApiResponse<?> isCourseFavorited(
            @AuthenticationPrincipal User user,
            @PathVariable Integer courseId) {

        boolean isFavorited = favoriteService.isCourseFavoritedByStudent(user, courseId);
        return ApiResponse.success(isFavorited);

    }
}
