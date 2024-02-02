package com.unir.products.data.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchStatement {

    private String key;
    private Object value;
    private SearchOperation operation;
}
