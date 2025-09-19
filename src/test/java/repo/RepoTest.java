package repo;

import models.Books;
import repository.BookRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // spins up only JPA-related beans
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = main.CrudAppApplication.class)
class RepoTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @Rollback(false) //I made rollback false just so I could see if the code is touching the database or not
    void shouldSaveAndFindBookById() throws Exception{
    	
    	//Creating a new book
        Books book = new Books();
        book.setIsbn("123");
        book.setAuthor("John Doe");
        book.setTitle("Repo Test");
        book.setPrice(15.5);

        // Save the book
        bookRepository.save(book);

        // Fetch it back (remember that all JPA find methods return an Optional<T> type
        Optional<Books> found = bookRepository.findById("123");

        
        assertThat(found).isPresent();
        //found.get() fetches the Book object from inside Optional<Book>
        assertThat(found.get().getAuthor()).isEqualTo("John Doe");
        assertThat(found.get().getTitle()).isEqualTo("Repo Test");
    }

    @Test
    void shouldFindAllBooks() throws Exception {
        Books book1 = new Books();
        book1.setIsbn("111");
        book1.setAuthor("Author1");
        book1.setTitle("Title1");
        book1.setPrice(10.0);

        Books book2 = new Books();
        book2.setIsbn("222");
        book2.setAuthor("Author2");
        book2.setTitle("Title2");
        book2.setPrice(20.0);

        bookRepository.save(book1);
        bookRepository.save(book2);
        //Mostly self-explanatory. Create 2 books, update DB and then store the findAll result in a List. Then check that the List is 2 elements long
        
        List<Books> allBooks = bookRepository.findAll();

        assertThat(allBooks.get(0).getIsbn().equals("111"));
        assertThat(allBooks.get(1).getIsbn().equals("222"));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Books book = new Books();
        book.setIsbn("333");
        book.setAuthor("Old Author");
        book.setTitle("Old Title");
        book.setPrice(30.0);

        bookRepository.save(book);

        // We created a book above. Here we update
        Books saved = bookRepository.findById("333").orElseThrow();
        saved.setAuthor("New Author");
        bookRepository.save(saved);

        //Just check whether the book's Author field has been updated or not.
        Books updated = bookRepository.findById("333").orElseThrow();
        assertThat(updated.getAuthor()).isEqualTo("New Author");
    }

    @Test
    void shouldDeleteBook() throws Exception {
        Books book = new Books();
        book.setIsbn("444");
        book.setAuthor("Delete Author");
        book.setTitle("Delete Title");
        book.setPrice(40.0);

        bookRepository.save(book);

        bookRepository.deleteById("444");

        Optional<Books> deleted = bookRepository.findById("444");
        assertThat(deleted).isEmpty();
    }
}
