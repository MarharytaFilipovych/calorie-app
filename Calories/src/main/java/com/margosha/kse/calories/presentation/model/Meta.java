package com.margosha.kse.calories.presentation.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Meta {
    private final int page;
    private final long totalCount;
    private final int pageSize;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public Meta(int page, long totalCount, int pageSize, int totalPages) {
        this.page = page;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.hasNext = page < totalPages;
        this.hasPrevious = page > 1;
    }
}
