
package com.coachera.backend.controller;

import com.coachera.backend.dto.WeekDTO;
import com.coachera.backend.service.WeekService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weeks")
public class WeekController {

    private final WeekService weekService;

    public WeekController(WeekService weekService) {
        this.weekService = weekService;
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/courses/{courseId}")
    public ResponseEntity<WeekDTO> createWeek(@PathVariable Integer courseId, @Valid @RequestBody WeekDTO weekDTO) {
        WeekDTO createdWeek = weekService.createWeek(courseId, weekDTO);
        return new ResponseEntity<>(createdWeek, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{weekId}")
    public ResponseEntity<WeekDTO> updateWeek(@PathVariable Integer weekId, @Valid @RequestBody WeekDTO weekDTO) {
        WeekDTO updatedWeek = weekService.updateWeek(weekId, weekDTO);
        return ResponseEntity.ok(updatedWeek);
    }

    @GetMapping("/{weekId}")
    public ResponseEntity<WeekDTO> getWeekById(@PathVariable Integer weekId) {
        WeekDTO weekDTO = weekService.getWeekById(weekId);
        return ResponseEntity.ok(weekDTO);
    }


    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<WeekDTO>> getAllWeeksByCourseId(@PathVariable Integer courseId) {
        List<WeekDTO> weeks = weekService.getAllWeeksByCourseId(courseId);
        return ResponseEntity.ok(weeks);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{weekId}")
    public ResponseEntity<Void> deleteWeek(@PathVariable Integer weekId) {
        weekService.deleteWeek(weekId);
        return ResponseEntity.noContent().build();
    }
}