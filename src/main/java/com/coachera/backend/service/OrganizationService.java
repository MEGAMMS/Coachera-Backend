package com.coachera.backend.service;

import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.dto.OrganizationRequestDTO;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CourseRepository courseRepository;

    public OrganizationDTO createOrganization(OrganizationRequestDTO organizationDTO, User user) {

        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User must be saved before creating Org profile");
        }

        if (organizationRepository.existsByOrgName(organizationDTO.getOrgName())) {
            throw new ConflictException("Organization with name '" + organizationDTO.getOrgName() + "' already exists");
        }

        Organization organization = modelMapper.map(organizationDTO, Organization.class);
        organization.setUser(user);

        Organization savedOrganization = organizationRepository.save(organization);
        return new OrganizationDTO(savedOrganization);
    }

    public OrganizationDTO getOrganizationById(Integer id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
        return new OrganizationDTO(organization);
    }

    public OrganizationDTO getOrganizationsByUser(User user) {
        Integer userId = user.getId();
        return new OrganizationDTO(organizationRepository.findByUserId(userId));
    }

    public OrganizationDTO updateOrganization(User user, OrganizationRequestDTO organizationDTO) {

        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User must be saved before creating Org profile");
        }
        Organization existingOrg = organizationRepository.findById(user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + user.getId()));

        // if (!existingOrg.getOrgName().equals(organizationDTO.getOrgName()) &&
        // organizationRepository.existsByOrgName(organizationDTO.getOrgName())) {
        // throw new ConflictException("Organization name already exists");
        // }

        modelMapper.map(organizationDTO, existingOrg);

        Organization updatedOrg = organizationRepository.save(existingOrg);
        return new OrganizationDTO(updatedOrg);
    }

    public void deleteOrganization(Integer id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        if (organization.getUser() != null) {
            User user = organization.getUser();

            courseRepository.deleteAll(courseRepository.findByOrgId(id));
            // Break the bidirectional relationship
            user.setOrganization(null);
            userRepository.save(user);
        }
        // Delete the student
        organizationRepository.delete(organization);
        if (organization.getUser() != null) {
            // Delete the user (now that access tokens are removed)
            userRepository.delete(organization.getUser());
        }
    }

    public void deleteOrganization(User user) {

        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User must be saved before creating Org profile");
        }
        Organization organization = organizationRepository.findById(user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + user.getOrganization().getId()));

        courseRepository.deleteAll(courseRepository.findByOrgId(organization.getId()));

        // Break the bidirectional relationship
        user.setOrganization(null);
        userRepository.save(user);

        // Delete the student
        organizationRepository.delete(organization);
        userRepository.delete(user);
    }

    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .map(org -> modelMapper.map(org, OrganizationDTO.class))
                .toList();
    }

    public Page<OrganizationDTO> getOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable)
                .map(OrganizationDTO::new);
    }

    public long countOrganizations() {
        return organizationRepository.count();
    }
}