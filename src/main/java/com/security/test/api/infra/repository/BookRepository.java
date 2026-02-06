package com.security.test.api.infra.repository;

import com.security.test.api.infra.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
