package com.margosha.kse.calories.user_graphql_subgraph.presentation.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@ToString
@Getter
public class Meta {
    private final int page;

    private final long totalCount;

    private final int pageSize;

    private final int totalPages;

    private final boolean hasNext;

    private final boolean hasPrevious;

    public Meta(Page<?> page) {
        this.page = page.getNumber() + 1;
        this.totalCount = page.getTotalElements();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }
}
