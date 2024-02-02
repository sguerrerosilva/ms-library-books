package com.unir.products.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.products.data.BookRepository;
import com.unir.products.model.pojo.Book;
import com.unir.products.model.request.CreateBookRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class BooksServiceImpl {

    @Autowired
    private BookRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Book> getBooks(String author, String title, String isbn, Short age, String synapsis, Short stock){

        if(StringUtils.hasLength(author) || StringUtils.hasLength(title) || StringUtils.hasLength(isbn)
                || age != null || StringUtils.hasLength(synapsis) || stock != null ){
            return repository.search(author,title,isbn,age,synapsis,stock);
        }
        List<Book> books = repository.getBooks();
        return books.isEmpty() ? null : books;
    }

    public Book getBook(Long idBook){
        return repository.getBook(idBook);
    }

    public Book createBook(CreateBookRequest bookRequest){
        //validar los datos

        Book book = Book.builder().author(bookRequest.getAuthor()).title(bookRequest.getTitle()).isbn(bookRequest.getIsbn())
                .age(bookRequest.getAge()).synapsis(bookRequest.getSynapsis()).stock(bookRequest.getStock()).build();
        return repository.saveBook(book);
    }

    public Book updateBook(Long idBook, String request){
        Book book = repository.getBook(idBook);
        if (book != null){
            try{
                JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(objectMapper.readTree(request));
                JsonNode target = jsonMergePatch.apply(objectMapper.readTree(objectMapper.writeValueAsString(book)));
                Book bookPatched = objectMapper.treeToValue(target, Book.class);
                return repository.saveBook(bookPatched);
            }catch (JsonProcessingException | JsonPatchException e){
                log.error("Error updating book: {}",idBook);
                return null;
            }
        }else{
            return null;
        }
    }

    public boolean removeBook(Long idBook){
        Book book = repository.getBook(idBook);
        if(book != null){
            repository.deleteBook(idBook);
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

}
