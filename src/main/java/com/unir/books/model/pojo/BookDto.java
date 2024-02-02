package com.unir.products.model.pojo;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {

    private String author;
    private String title;
    private String isbn;
    private Short age;
    private String synapsis;
    private Short stock;
}
