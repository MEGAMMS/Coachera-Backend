package com.coachera.backend.dto.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "Pagination request parameters")
public class PaginationRequest {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "Number of items per page", example = "10", defaultValue = "10", maximum = "100")
    private Integer size = DEFAULT_PAGE_SIZE;

    @Schema(description = "Field to sort by", example = "createdAt", defaultValue = "id")
    private String sortBy = DEFAULT_SORT_FIELD;

    @Schema(description = "Sort direction (asc/desc)", example = "desc", defaultValue = "desc", allowableValues = {
            "asc", "desc" })
    private String sortDirection = "desc";

    public Pageable toPageable() {
        // Validate and set default values
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        // Handle sorting
        String sortField = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : DEFAULT_SORT_FIELD;
        Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : DEFAULT_SORT_DIRECTION;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
    }
}
