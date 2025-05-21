package com.coachera.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
    Organization findByUserId(Integer userId);
    boolean existsByOrgName(String orgName);
}
