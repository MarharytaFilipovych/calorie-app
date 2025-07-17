package com.margosha.kse.calories.presentation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private Meta meta;
    private List<T> content;
    
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(new Meta(page), page.getContent());
    }
}