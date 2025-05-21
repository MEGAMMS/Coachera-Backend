package com.coachera.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
    Organization findByUserId(Integer userId);
    boolean existsByOrgName(String orgName);
}
