package com.unir.books.data;

import com.unir.books.model.db.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;
import java.util.Optional;

interface BookRepository extends ElasticsearchRepository<Book, String> {


    Optional<Book> findById(String id);

    Book save(Book product);

    void delete(Book product);

    List<Book> findAll();

}
