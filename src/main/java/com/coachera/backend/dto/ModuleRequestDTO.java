package com.coachera.backend.dto;

import java.time.LocalDateTime;

import com.coachera.backend.entity.Module;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ModuleRequestDTO extends ModuleDTO {
    @Override
    @JsonIgnore
    public Integer getId() {
        return super.getId();
    }

    @Override
    @JsonIgnore
    public LocalDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    @Override
    @JsonIgnore
    public LocalDateTime getUpdatedAt() {
        return super.getUpdatedAt();
    }

    @Override
    @JsonIgnore
    public Integer getCourseId()  {
        return super.getCourseId();
    }
}
