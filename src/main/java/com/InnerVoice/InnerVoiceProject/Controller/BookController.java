package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Model.*;
import com.InnerVoice.InnerVoiceProject.Services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/books")
@CrossOrigin
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public void addBook(@RequestBody Book book)
    {
        bookService.addBook(book);
    }

    @GetMapping("/getting/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable int id)
    {
        Optional<Book> book = bookService.getBookById(id);
        if(book.isPresent())
        {
            Book b=book.get();
            return ResponseEntity.ok(b);
        }
        return ResponseEntity.status(404).body("Book not found");
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Book>> addBooksBatch(@RequestBody List<Book> books) {
        List<Book> savedBooks = bookService.addBooksBatch(books);
        return ResponseEntity.ok(savedBooks);
    }

    @GetMapping
    public List<Book> getAllBooks()
    {
        return bookService.getBooks();
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id)
    {
        bookService.deleteBook((id));
    }

    @GetMapping("/{bookType}")
    public List<Book> getBookByType(@PathVariable String bookType)
    {
        return bookService.getBookByType(bookType);
    }
    @GetMapping("/search/{keyword}")
    public List<Book> searchBooks(@PathVariable String keyword) {
        return bookService.searchBooks(keyword);
    }

    @PutMapping("/update-audio-links")
    public ResponseEntity<List<Book>> updateAudioLinks() {
        List<Book> updatedBooks = bookService.updateAllAudioLinksToStream();
        return ResponseEntity.ok(updatedBooks);
    }

    @DeleteMapping
    public void deleteAll()
    {
        bookService.deleteAllBooks();
    }
}
