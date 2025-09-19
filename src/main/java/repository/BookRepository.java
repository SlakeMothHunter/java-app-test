package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import models.Books;

public interface BookRepository extends JpaRepository<Books, String>{

}
