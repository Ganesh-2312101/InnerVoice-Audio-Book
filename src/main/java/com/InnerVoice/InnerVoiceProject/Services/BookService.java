package com.InnerVoice.InnerVoiceProject.Services;

import com.InnerVoice.InnerVoiceProject.Model.*;
import com.InnerVoice.InnerVoiceProject.Repositories.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public void addBook(Book book)
    {
        bookRepository.save(book);
    }
    public List<Book> addBooksBatch(List<Book> books) {
        return bookRepository.saveAll(books);
    }
    public List<Book> getBooks()
    {
        return bookRepository.findAll();
    }
    public Optional<Book> getBookById(int id)
    {
        return bookRepository.findById(id);
    }
    public void deleteBook(int id)
    {
        bookRepository.deleteById(id);
    }
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooksByNameOrAuthor(keyword);
    }
    public List<Book> getBookByType(String bookType)
    {
        return bookRepository.getBookByType(bookType);
    }

    public List<Book> updateAllAudioLinksToStream() {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            String link = book.getAudioFileLink();
            if (link != null && link.contains("export=download")) {
                link = link.replace("export=download", "export=view");
                book.setAudioFileLink(link);
            }
        }
        return bookRepository.saveAll(books);
    }

    public void deleteAllBooks()
    {
        bookRepository.deleteAll();
    }

}