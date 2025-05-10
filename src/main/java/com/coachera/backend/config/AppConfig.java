package com.coachera.backend.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.Student;

@Configuration
public class AppConfig {

   @Bean
public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration()
          .setSkipNullEnabled(true);     
    mapper.typeMap(StudentDTO.class, Student.class)
          .addMappings(m -> m.skip(Student::setId));
    return mapper;
}

}

