package com.unir.products.data;

import com.unir.products.data.utils.SearchCriteria;
import com.unir.products.data.utils.SearchOperation;
import com.unir.products.data.utils.SearchStatement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import com.unir.products.model.pojo.Book;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final BookJpaRepository repository;

    public List<Book> getBooks(){
        return repository.findAll();
    }

    public Book getBook(Long idBook){
        return repository.findById(idBook).orElse(null);
    }

    public Book saveBook(Book book ){
        return repository.save(book);
    }

    public void deleteBook(Long idBook){repository.deleteById(idBook);}

    public List<Book> search(String author, String title, String isbn, Short age, String synapsis, Short stock) {
        SearchCriteria<Book> spec = new SearchCriteria<>();
        if (StringUtils.isNotBlank(author)) {
            spec.add(new SearchStatement("author", author, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(title)) {
            spec.add(new SearchStatement("title", title, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(isbn)) {
            spec.add(new SearchStatement("isbn", isbn, SearchOperation.EQUAL));
        }

        if (age != null) {
            spec.add(new SearchStatement("age", age, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(synapsis)) {
            spec.add(new SearchStatement("synapsis", synapsis, SearchOperation.MATCH));
        }

        if (stock != null) {
            spec.add(new SearchStatement("stock", stock, SearchOperation.EQUAL));
        }

        return repository.findAll(spec);
    }
}
