package com.unir.books.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationDetails {

    private String key;
    private Integer count;
    private String uri;
}
