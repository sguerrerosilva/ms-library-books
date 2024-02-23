package com.unir.books.model.db;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "books", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {

    @Id
    private String id;

    @Field(type = FieldType.Search_As_You_Type, name = "Title")
    private String title;

    @Field(type = FieldType.Short, name= "Age")
    private String age;

    @Field(type = FieldType.Keyword, name = "Author")
    private String author;

    @Field(type = FieldType.Keyword, name = "Gender")
    private String gender;

    @Field(type = FieldType.Text, name = "Image")
    private String image;

    @Field(type = FieldType.Keyword, name = "Isbn")
    private String isbn;

    @Field(type = FieldType.Short, name = "Stock")
    private Short stock;

    @Field(type = FieldType.Text, name = "Synapsis")
    private String synapsis;




}
