package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Organization;

public class CourseGenerator {
    // public static List<Course> forOrgs(List<Organization> orgs, int coursesPerOrg) {
    //     return orgs.stream()
    //         .flatMap(org -> Instancio.ofList(Course.class).size(coursesPerOrg)
    //             .ignore(Select.field(Course::getId))
    //             .supply(Select.field(Course::getOrg), () -> org)
    //             .create().stream()
    //         ).collect(Collectors.toList());
    // }
}
