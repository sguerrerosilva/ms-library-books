package com.unir.books.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {

    private String author;
    private String title;
    private String isbn;
    private String age;
    private String synapsis;
    private Short stock;

}
