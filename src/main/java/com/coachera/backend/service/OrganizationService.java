package com.coachera.backend.service;

import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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


    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
      
        User user = userRepository.findById(organizationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + organizationDTO.getUserId()));

        
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

    public OrganizationDTO updateOrganization(Integer id, OrganizationDTO organizationDTO) {
        Organization existingOrg = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));

      
        if (!existingOrg.getOrgName().equals(organizationDTO.getOrgName()) &&
            organizationRepository.existsByOrgName(organizationDTO.getOrgName())) {
            throw new ConflictException("Organization name already exists");
        }

        modelMapper.map(organizationDTO, existingOrg);
        
        
        if (organizationDTO.getUserId() != null && 
            !organizationDTO.getUserId().equals(existingOrg.getUser().getId())) {
            User user = userRepository.findById(organizationDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            existingOrg.setUser(user);
        }

        Organization updatedOrg = organizationRepository.save(existingOrg);
        return new OrganizationDTO(updatedOrg);
    }

    public void deleteOrganization(Integer id) {
        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organization not found");
        }
        organizationRepository.deleteById(id);
    }

   public List<OrganizationDTO> getAllOrganizations() {
    return organizationRepository.findAll()
            .stream()
            .map(org -> modelMapper.map(org, OrganizationDTO.class))
            .toList();
}

}