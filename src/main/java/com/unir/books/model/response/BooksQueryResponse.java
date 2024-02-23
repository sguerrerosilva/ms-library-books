package com.unir.books.model.response;

import com.unir.books.model.db.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooksQueryResponse {

    private List<Book> products;
    private List<AggregationDetails> aggs;

}
