package com.unir.products.model.pojo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author", unique = true)
    private String author;

    @Column(name = "title")
    private String title;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "age")
    private Short age;

    @Column(name = "synapsis")
    private String synapsis;

    @Column(name ="stock")
    private Short stock;

    public void update(BookDto bookDto) {
        this.author = bookDto.getAuthor();
        this.title = bookDto.getTitle();
        this.isbn = bookDto.getIsbn();
        this.age = bookDto.getAge();
        this.synapsis = bookDto.getSynapsis();
        this.stock =bookDto.getStock();
    }

}
