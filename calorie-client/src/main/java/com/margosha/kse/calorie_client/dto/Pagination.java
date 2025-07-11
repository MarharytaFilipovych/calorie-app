package com.margosha.kse.calorie_client.dto;

import lombok.Data;

@Data
public class Pagination {
    private int limit = 20;
    private int offset = 1;
}
