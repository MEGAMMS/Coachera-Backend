package com.coachera.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
