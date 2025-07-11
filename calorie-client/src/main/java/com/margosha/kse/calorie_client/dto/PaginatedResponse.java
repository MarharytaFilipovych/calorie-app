package com.margosha.kse.calorie_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PaginatedResponse<T> {
    private Meta meta;

    @JsonIgnore
    private List<T> entities;

    @JsonProperty("users")
    public void setUsers(List<T> users) {
        this.entities = users;
    }

    @JsonProperty("products")
    public void setProducts(List<T> products) {
        this.entities = products;
    }

    @JsonProperty("records")
    public void setRecords(List<T> records) {
        this.entities = records;
    }
}
