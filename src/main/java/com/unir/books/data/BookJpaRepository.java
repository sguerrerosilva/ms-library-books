package com.unir.products.data;

import com.unir.products.model.pojo.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookJpaRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

}
