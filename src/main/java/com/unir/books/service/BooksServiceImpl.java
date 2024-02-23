package com.unir.books.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.books.data.DataAccessRepository;
import com.unir.books.model.db.Book;
import com.unir.books.model.request.CreateBookRequest;
import com.unir.books.model.response.BooksQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class BooksServiceImpl {

    @Autowired
    private DataAccessRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public BooksQueryResponse getBooks(String param,String gender, Boolean aggregate){
        return repository.findBooks(param, gender ,aggregate);
    }

    public Book getBook(String idBook){
        return repository.getBook(idBook).orElse(null);
    }

    public Book createBook(CreateBookRequest bookRequest){
        //validar los datos

        Book book = Book.builder().author(bookRequest.getAuthor()).title(bookRequest.getTitle()).isbn(bookRequest.getIsbn())
                .age(bookRequest.getAge()).synapsis(bookRequest.getSynapsis()).stock(bookRequest.getStock()).build();
        return repository.saveBook(book);
    }

    public Book updateBook(String idBook, String request){
        Book book = repository.getBook(idBook).orElse(null);
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

    public boolean removeBook(String idBook){
        Book book = repository.getBook(idBook).orElse(null);
        if(book != null){
            repository.deleteBook(book);
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

}
