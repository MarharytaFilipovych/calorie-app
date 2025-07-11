package com.margosha.kse.calorie_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Meta(int page,
                   @JsonProperty("total_count") long totalCount,
                   @JsonProperty("page_size") int pageSize,
                   @JsonProperty("total_pages") int totalPages,
                   @JsonProperty("has_next") boolean hasNext,
                   @JsonProperty("has_previous") boolean hasPrevious) { }
