package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import models.BookUpdateDTO;
import models.Books;
import models.BooksDTO;
import repository.BookRepository;

@RestController
public class BooksController {
	
	private BookRepository bookrepo;
	
	public BooksController(BookRepository bookrepo) {
		this.bookrepo = bookrepo;
	}

	@GetMapping({"/books", "/books/"})
	public List<Books> showBooks() {
		List<Books> books = bookrepo.findAll();
		return books;
	}
	
	@PostMapping("/books")
    public ResponseEntity<?> addBookSubmit(@Valid @RequestBody BooksDTO bookDTO,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
        	Map<String, String> map = new HashMap<>();
        	bindingResult.getFieldErrors().forEach(error -> map.put(error.getField(), error.getDefaultMessage()));
        	return ResponseEntity.badRequest().body(map);
        	
        }


        Books book = new Books();
        book.setIsbn(bookDTO.getIsbn());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPrice(bookDTO.getPrice());


        bookrepo.save(book);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(book);
	}
	
	@PatchMapping({"/books/{isbn}", "/books/{isbn}"})
	public ResponseEntity<?> updateBook(
	        @PathVariable String isbn,
	        @RequestBody @Valid BookUpdateDTO updateDTO,
	        BindingResult bindingResult) {

	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = new HashMap<>();
	        bindingResult.getFieldErrors()
	                     .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
	        return ResponseEntity.badRequest().body(errors);
	    }

	    Books book = bookrepo.findById(isbn).orElse(null);
	    if (book == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(Map.of("error", "Book not found with ISBN: " + isbn));
	    }

	    // apply only non-null updates
	    if (updateDTO.getTitle() != null) {
	        book.setTitle(updateDTO.getTitle());
	    }
	    if (updateDTO.getAuthor() != null) {
	        book.setAuthor(updateDTO.getAuthor());
	    }
	    if (updateDTO.getPrice() != null) {
	        book.setPrice(updateDTO.getPrice());
	    }

	    bookrepo.save(book);
	    return ResponseEntity.ok(book);
	}
	
    @DeleteMapping({"/books/{isbn}", "/books/{isbn}"})
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
    	
    	
    	Books book = bookrepo.findById(isbn).orElse(null);
    	
    	if (book == null) {
    		Map <String, String> map = new HashMap<>();
    		map.put("error", "isbn cannot be null");
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    	}
    	
       bookrepo.delete(book);
       return ResponseEntity.status(HttpStatus.NO_CONTENT).body(book); 
    }
    

	
}
