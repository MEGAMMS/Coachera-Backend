package com.coachera.backend.service;

import com.coachera.backend.dto.ReviewDTO;
import com.coachera.backend.entity.*;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.repository.ReviewRepository;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public ReviewService(ReviewRepository reviewRepository,
                        CourseRepository courseRepository,
                        StudentRepository studentRepository,
                        ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
    }

    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        // Check if student already reviewed this course
        if (reviewRepository.existsByCourseIdAndStudentId(reviewDTO.getCourseId(), reviewDTO.getStudentId())) {
            throw new ConflictException("Student has already reviewed this course");
        }

        Course course = courseRepository.findById(reviewDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + reviewDTO.getCourseId()));

        Student student = studentRepository.findById(reviewDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + reviewDTO.getStudentId()));

        Review review = new Review();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        Review savedReview = reviewRepository.save(review);
        return new ReviewDTO(savedReview);
    }

    public ReviewDTO getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return new ReviewDTO(review);
    }

    public List<ReviewDTO> getReviewsByCourse(Integer courseId) {
        return reviewRepository.findByCourseId(courseId).stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByStudent(Integer studentId) {
        return reviewRepository.findByStudentId(studentId).stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
    }

    public ReviewDTO updateReview(Integer id, ReviewDTO reviewDTO) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        // Verify the course and student haven't changed
        if (!existingReview.getCourse().getId().equals(reviewDTO.getCourseId()) || 
            !existingReview.getStudent().getId().equals(reviewDTO.getStudentId())) {
            throw new IllegalArgumentException("Cannot change course or student for an existing review");
        }

        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());

        Review updatedReview = reviewRepository.save(existingReview);
        return new ReviewDTO(updatedReview);
    }

    public void deleteReview(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    public double getAverageRatingForCourse(Integer courseId) {
        return reviewRepository.findByCourseId(courseId).stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}