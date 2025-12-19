package com.project.library_management_system.book;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// We need to perform an INTEGRATION testing for the repositories
//Unit testing is not enough here

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp(){
        book1 = new Book(null, "The Shadow King", "Maaza Mengiste", "9878176076297", true, null);
        book2 = new Book(null, "Beneath the Lion's Gaze", "Maaza Mengiste", "9878173232323", false, null);
    }

    @Test
    void givenNewBook_whenSave_thenSuccess(){
        Book newBook = book1;
        Book insertedBook = bookRepository.save(newBook);

        assertThat(entityManager.find(Book.class, insertedBook.getId())).isEqualTo(newBook);
    }

    @Test
    void givenBookCreated_whenUpdate_thenSuccess(){
        Book newBook = book1;
        entityManager.persist(newBook);
        newBook.setAvailable(false);

        bookRepository.save(newBook);
        assertThat(entityManager.find(Book.class, newBook.getId()).isAvailable()).isEqualTo(false);
    }

    @Test
    void givenBookCreated_whenFindById_thenSuccess(){
        entityManager.persist(book1);
        Optional<Book> retrievedBook = bookRepository.findById(book1.getId());
        assertThat(retrievedBook).contains(book1);
    }

    @Test
    void givenBookCreated_whenFindAllByAuthor_thenSuccess(){
        entityManager.persist(book1);
        entityManager.persist(book2);
        Iterable<Book> books = bookRepository.findAllByAuthor("Maaza Mengiste");
        assertThat(books).contains(book1, book2);
    }

    @Test
    void givenBookCreated_whenFindAllByTitle_thenSuccess(){
        entityManager.persist(book1);
        entityManager.persist(book2);
        Iterable<Book> books = bookRepository.findAllByTitle("The Shadow King");
        assertThat(books).contains(book1);
    }

    @Test
    void givenBookCreated_whenFindAllByAvailable_thenSuccess(){
        entityManager.persist(book1);
        entityManager.persist(book2);
        Iterable<Book> books = bookRepository.findAllByAvailable(true);
        assertThat(books).contains(book1);
    }

    @Test
    void givenBookCreated_whenDelete_thenSuccess(){
        entityManager.persist(book1);
        bookRepository.delete(book1);
        assertThat(entityManager.find(Book.class, book1.getId())).isNull();
    }
}
