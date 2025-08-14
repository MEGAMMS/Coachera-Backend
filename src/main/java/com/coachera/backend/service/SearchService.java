package com.coachera.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.*;
import com.coachera.backend.entity.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SearchService {

    private final EntityManager entityManager;
    private final Map<Class<?>, Function<?, ?>> dtoMappers = new java.util.HashMap<>();

    public SearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
        // Register DTO mappers
        registerDtoMapper(Course.class, CourseDTO::new);
        registerDtoMapper(Student.class, StudentDTO::new);
        registerDtoMapper(Instructor.class, InstructorDTO::new);
        registerDtoMapper(Category.class, CategoryDTO::new);
        registerDtoMapper(Skill.class, SkillDTO::new);
        registerDtoMapper(LearningPath.class, LearningPathDTO::new);
        registerDtoMapper(Material.class, MaterialDTO::new);
        registerDtoMapper(Section.class, SectionDTO::new);
        registerDtoMapper(Quiz.class, QuizDTO::new);
        registerDtoMapper(Question.class, QuestionDTO::new);
        registerDtoMapper(Review.class, ReviewDTO::new);
        registerDtoMapper(Certificate.class, CertificateDTO::new);
        registerDtoMapper(User.class, UserDTO::new);
        registerDtoMapper(Enrollment.class, EnrollmentDTO::new);
        registerDtoMapper(Favorite.class, FavoriteDTO::new);
    }

    private <T, D> void registerDtoMapper(Class<T> entityClass, Function<T, D> mapper) {
        dtoMappers.put(entityClass, mapper);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public <T, D> Page<D> search(Class<T> entityClass, SearchRequest searchRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        // Build predicates for main query
        List<Predicate> mainPredicates = buildPredicates(cb, root, searchRequest);
        if (!mainPredicates.isEmpty()) {
            cq.where(mainPredicates.toArray(new Predicate[0]));
        }

        Pageable pageable = searchRequest.toPageable();

        // Sorting
        if (pageable.getSort().isSorted()) {
            List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    orders.add(cb.asc(root.get(order.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(order.getProperty())));
                }
            });
            cq.orderBy(orders);
        }

        // Build count query with separate root and predicates
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, searchRequest);
        countQuery.select(cb.count(countRoot));
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(new Predicate[0]));
        }
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // Execute main query
        List<T> results = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Map results to DTOs
        Function<T, D> mapper = (Function<T, D>) dtoMappers.get(entityClass);
        if (mapper == null) {
            throw new IllegalArgumentException("No DTO mapper registered for entity class: " + entityClass.getName());
        }
        List<D> dtoResults = results.stream().map(mapper).collect(Collectors.toList());

        return new PageImpl<>(dtoResults, pageable, total);
    }

    private <T> List<Predicate> buildPredicates(CriteriaBuilder cb, Root<T> root, SearchRequest searchRequest) {
        List<Predicate> predicates = new ArrayList<>();

        // Search term predicate
        if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().isEmpty()) {
            String searchTerm = "%" + searchRequest.getSearchTerm().toLowerCase() + "%";
            List<Predicate> orPredicates = root.getModel().getDeclaredSingularAttributes().stream()
                    .filter(attr -> attr.getJavaType() == String.class)
                    .map(attr -> cb.like(cb.lower(root.get(attr.getName())), searchTerm))
                    .collect(Collectors.toList());
            if (!orPredicates.isEmpty()) {
                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
            }
        }

        // Filter predicates
        if (searchRequest.getFilters() != null) {
            searchRequest.getFilters().forEach((key, value) -> {
                if (value != null) {
                    if (value instanceof String) {
                        predicates.add(cb.like(cb.lower(root.get(key)), "%" + ((String) value).toLowerCase() + "%"));
                    } else {
                        predicates.add(cb.equal(root.get(key), value));
                    }
                }
            });
        }

        return predicates;
    }
}
