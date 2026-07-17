package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Model.*;
import com.InnerVoice.InnerVoiceProject.Repositories.BookRepository;
import com.InnerVoice.InnerVoiceProject.Repositories.UserActivityRepository;
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
    @Autowired
    private UserActivityRepository userActivityRepository;
    @Autowired
    private BookRepository bookRepository;
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

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Book>> getRecommendations(@PathVariable int userId)
    {
        List<Book> listenedBooks = userActivityRepository.findBooksListenedByUser(userId);

        Set<Integer> listenedBooksId=new HashSet<>();
        Set<String> authors=new HashSet<>();
        Set<String> categories=new HashSet<>();

        for(Book b:listenedBooks)
        {
            listenedBooksId.add(b.getBookId());
            authors.add(b.getBookAuthor());
            categories.add(b.getBookType());
        }
        

        List<Book> authorBased=new ArrayList<>();
        List<Book> categoryBased=new ArrayList<>();
        for(String author:authors)
        {
            List<Book> x= bookRepository.searchBooksByNameOrAuthor(author);
            for(Book b:x)
            {
                authorBased.add(b);
            }
        }
        for(String category: categories)
        {
            List<Book> y= bookRepository.getBookByType(category);
            for(Book b:y)
            {
                categoryBased.add(b);
            }
        }
        Set<Book> recommendationsSet = new HashSet<>();
        for(Book book : authorBased) {
            if (!listenedBooksId.contains(book.getBookId())) {
                recommendationsSet.add(book);
            }
        }
        for(Book book : categoryBased) {
            if (!listenedBooksId.contains(book.getBookId())) {
                recommendationsSet.add(book);
            }
        }
        List<Book> recommendations = new ArrayList<>(recommendationsSet);
        return ResponseEntity.ok(recommendations);
    }

}
