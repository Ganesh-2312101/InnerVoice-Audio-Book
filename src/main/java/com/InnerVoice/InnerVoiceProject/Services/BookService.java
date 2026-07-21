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

    public List<Book> updateAllImageLinksToLocal() {
        List<Book> books = bookRepository.findAll();

        // Map book names (lowercase, stripped) to local image filenames
        Map<String, String> imageMap = new HashMap<>();
        imageMap.put("atomic habits", "/images/cover_atomic_habits.jpg");
        imageMap.put("deep work", "/images/cover_deep_work.jpg");
        imageMap.put("the 7 habits", "/images/cover_the_7_habits_of_highly_effective_people.jpg");
        imageMap.put("the 7 habits of highly effective people", "/images/cover_the_7_habits_of_highly_effective_people.jpg");
        imageMap.put("eat that frog", "/images/cover_eat_that_frog.jpg");
        imageMap.put("the 5 am club", "/images/cover_the_5_am_club.jpg");
        imageMap.put("getting things done", "/images/cover_getting_things_done.jpg");
        imageMap.put("the power of habit", "/images/cover_the_power_of_habit.jpg");
        imageMap.put("dune", "/images/cover_dune.jpg");
        imageMap.put("foundation", "/images/cover_foundation.jpg");
        imageMap.put("the martian", "/images/cover_the_martian.jpg");
        imageMap.put("the time machine", "/images/cover_the_time_machine.jpg");
        imageMap.put("ender's game", "/images/cover_enders_game.jpg");
        imageMap.put("ready player one", "/images/cover_ready_player_one.jpg");
        imageMap.put("neuromancer", "/images/cover_neuromancer.jpg");
        imageMap.put("sherlock holmes", "/images/cover_sherlock_holmes.jpg");
        imageMap.put("gone girl", "/images/cover_gone_girl.jpg");
        imageMap.put("the da vinci code", "/images/cover_the_da_vinci_code.jpg");
        imageMap.put("murder on the orient express", "/images/cover_murder_on_the_orient_express.jpg");
        imageMap.put("killing floor", "/images/cover_killing_floor.jpg");
        imageMap.put("the girl with the dragon tattoo", "/images/cover_the_girl_with_the_dragon_tattoo.jpg");
        imageMap.put("the firm", "/images/cover_the_firm.jpg");
        imageMap.put("the alchemist", "/images/cover_the_alchemist.jpg");
        imageMap.put("1984", "/images/cover_1984.jpg");
        imageMap.put("pride and prejudice", "/images/cover_pride_and_prejudice.jpg");
        imageMap.put("the hobbit", "/images/cover_the_hobbit.jpg");
        imageMap.put("the great gatsby", "/images/cover_the_great_gatsby.jpg");
        imageMap.put("to kill a mockingbird", "/images/cover_to_kill_a_mockingbird.jpg");
        imageMap.put("the kite runner", "/images/cover_the_kite_runner.jpg");

        for (Book book : books) {
            String key = book.getBookName().toLowerCase().trim();
            if (imageMap.containsKey(key)) {
                book.setImageLink(imageMap.get(key));
            } else {
                // Fallback: keep existing or set default
                if (book.getImageLink() == null || book.getImageLink().startsWith("http")) {
                    book.setImageLink("/default.webp");
                }
            }
        }
        return bookRepository.saveAll(books);
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