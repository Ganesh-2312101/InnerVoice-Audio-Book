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

    @PutMapping("/update-image-links")
    public ResponseEntity<List<Book>> updateImageLinks() {
        List<Book> updatedBooks = bookService.updateAllImageLinksToLocal();
        return ResponseEntity.ok(updatedBooks);
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

    @GetMapping("/summary/{id}")
    public ResponseEntity<String> generateSummary(@PathVariable int id, @RequestParam String type) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        if(bookOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Book not found for summary generation.");
        }
        Book book = bookOpt.get();
        String title = book.getBookName();
        String author = book.getBookAuthor();
        String dummyContent = "";

        if ("50".equals(type)) {
            dummyContent = "<strong>50-Word Summary:</strong><br/>" + title + " by " + author + " is a compelling journey that captures the essence of its genre. Through well-developed characters and an engaging narrative, it explores profound themes that resonate deeply with readers. A must-read for fans seeking a thought-provoking, concise experience packed with emotion and insightful commentary on the human condition.";
        } else if ("100".equals(type)) {
            dummyContent = "<strong>100-Word Summary:</strong><br/>" + title + " by " + author + " presents an immersive world filled with complex characters and a gripping storyline. At its core, the narrative delves into the struggles of the protagonist against seemingly insurmountable odds, exploring themes of resilience, identity, and the power of choices. As the plot unfolds through unexpected twists and emotional depths, readers are drawn into a masterfully crafted universe. The rich descriptions and fast-paced events keep the audience on the edge of their seats, culminating in a satisfying yet thought-provoking conclusion that leaves a lasting impact long after the final page is turned.";
        } else if ("keypoints".equals(type)) {
            dummyContent = "<strong>Key Points:</strong><ul>" +
                    "<li>📚 <strong>Engaging Narrative:</strong> The story of " + title + " keeps readers hooked from the very beginning.</li>" +
                    "<li>👤 <strong>Character Growth:</strong> The protagonist undergoes significant development throughout the journey.</li>" +
                    "<li>🌍 <strong>World Building:</strong> " + author + " creates a rich and vivid setting that brings the story to life.</li>" +
                    "<li>💡 <strong>Thematic Depth:</strong> Explores universal themes such as resilience, identity, and hope.</li>" +
                    "<li>🔥 <strong>Plot Twists:</strong> Several unexpected turns keep the plot fresh and exciting.</li>" +
                    "</ul>";
        } else {
            return ResponseEntity.badRequest().body("Invalid summary type requested.");
        }

        // Simulate AI generation delay
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(dummyContent);
    }

}
