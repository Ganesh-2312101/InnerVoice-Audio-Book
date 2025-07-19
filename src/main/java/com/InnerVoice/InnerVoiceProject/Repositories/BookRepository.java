package com.InnerVoice.InnerVoiceProject.Repositories;

import com.InnerVoice.InnerVoiceProject.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface BookRepository extends JpaRepository<Book, Integer>{
    @Query("SELECT b FROM Book b WHERE LOWER(b.BookName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.BookAuthor) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooksByNameOrAuthor(@Param("keyword") String keyword);
    @Query("SELECT b from Book b where b.BookType= :bookType")
    List<Book> getBookByType(@Param("bookType")String bookType);
}
