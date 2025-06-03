package com.coachera.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.UserRepository;

@Service
@Transactional
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public InstructorService(InstructorRepository instructorRepository, 
                           UserRepository userRepository,
                           ModelMapper modelMapper) {
        this.instructorRepository = instructorRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public InstructorDTO createInstructor(InstructorDTO instructorDTO, User user) {
        if (instructorRepository.existsByUserId(instructorDTO.getUserId())) {
            throw new ConflictException("User already has an instructor profile");
        }
        
        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setBio(instructorDTO.getBio());
        
        Instructor savedInstructor = instructorRepository.save(instructor);
        return new InstructorDTO(savedInstructor);
    }

    public InstructorDTO getInstructorByUser(User user) {
        Integer instructorId = instructorRepository.findByUserId(user.getId()).get().getId();
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        return new InstructorDTO(instructor);
    }

    public List<InstructorDTO> getAllInstructors() {
        return instructorRepository.findAll().stream()
                .map(instructor -> new InstructorDTO(instructor))
                .collect(Collectors.toList());
    }

    public Page<InstructorDTO> getInstructors(Pageable pageable) {
        return instructorRepository.findAll(pageable).
                map(InstructorDTO::new);
    }

    public InstructorDTO updateInstructor(User user, InstructorDTO instructorDTO) {
        Integer instructorId = instructorRepository.findByUserId(user.getId()).get().getId();
        Instructor existingInstructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        modelMapper.map(instructorDTO, existingInstructor);
        
        if (instructorDTO.getUserId() != null && 
            !instructorDTO.getUserId().equals(existingInstructor.getUser().getId())) {
            User newUser = userRepository.findById(instructorDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + instructorDTO.getUserId()));
            existingInstructor.setUser(newUser);
        }

        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        return new InstructorDTO(updatedInstructor);
    }

    public void deleteInstructor(Integer id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor not found with id: " + id);
        }
        instructorRepository.deleteById(id);
    }
    
    // Additional methods specific to Instructor if needed
    public InstructorDTO getInstructorById(Integer id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        return new InstructorDTO(instructor);
    }
}