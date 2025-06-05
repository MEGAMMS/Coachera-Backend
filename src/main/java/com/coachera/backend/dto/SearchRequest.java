package com.coachera.backend.dto;

import java.util.Map;
import java.util.HashMap;

import com.coachera.backend.dto.pagination.PaginationRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Search request parameters")
public class SearchRequest extends PaginationRequest {
    
    @Schema(description = "Search term to look for in string fields", example = "java programming")
    private String searchTerm;

    @Schema(description = "Map of field names to filter values", example = "{\"orgId\": 1, \"isPublished\": true}")
    private Map<String, Object> filters = new HashMap<>();
} 