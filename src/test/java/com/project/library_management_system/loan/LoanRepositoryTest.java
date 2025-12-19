package com.project.library_management_system.loan;

import com.project.library_management_system.book.Book;
import com.project.library_management_system.user.Role;
import com.project.library_management_system.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Loan activeLoan;
    private Loan returnedLoan;

    @BeforeEach
    void setUp() {
        // 1. Create and Persist a USER first
        // We use 'null' for ID to let the DB auto-generate it
        User user = new User(null, "Alice", "alice@test.com","password123", Role.USER, true, new ArrayList<>());
        entityManager.persist(user);

        // 2. Create and Persist BOOKS first
        Book book1 = new Book(null, "Spring Boot Action", "Craig Walls", "9781617292545", true, null);
        Book book2 = new Book(null, "Java Optimized", "Ben Evans", "9781491954385", true, null);
        entityManager.persist(book1);
        entityManager.persist(book2);

        // 3. Create LOANS linking the persisted User and Books
        activeLoan = new Loan();
        activeLoan.setUser(user);
        activeLoan.setBook(book1);
        activeLoan.setLoanDate(LocalDate.now());
        activeLoan.setReturned(false);
        // ID is null so it auto-generates

        returnedLoan = new Loan();
        returnedLoan.setUser(user);
        returnedLoan.setBook(book2);
        returnedLoan.setLoanDate(LocalDate.now().minusDays(10));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setReturned(true);
    }

    @Test
    void givenNewLoan_whenSave_thenSuccess() {
        // Act
        Loan savedLoan = loanRepository.save(activeLoan);

        // Assert
        Loan foundLoan = entityManager.find(Loan.class, savedLoan.getId());
        assertThat(foundLoan).isNotNull();
        assertThat(foundLoan.getBook().getTitle()).isEqualTo("Spring Boot Action");
    }

    @Test
    void givenLoanCreated_whenFindById_thenSuccess() {
        // Arrange
        Loan persistedLoan = entityManager.persist(activeLoan);

        // Act
        Optional<Loan> found = loanRepository.findById(persistedLoan.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(persistedLoan);
    }

    @Test
    void givenLoanCreated_whenUpdate_thenSuccess() {
        // Arrange
        Loan persistedLoan = entityManager.persist(activeLoan);

        // Act - Mark it as returned
        persistedLoan.setReturned(true);
        persistedLoan.setReturnDate(LocalDate.now());
        loanRepository.save(persistedLoan);

        // Assert
        Loan updatedLoan = entityManager.find(Loan.class, persistedLoan.getId());
        assertThat(updatedLoan.isReturned()).isTrue();
        assertThat(updatedLoan.getReturnDate()).isNotNull();
    }

    @Test
    void givenLoanCreated_whenDelete_thenSuccess() {
        // Arrange
        Loan persistedLoan = entityManager.persist(activeLoan);

        // Act
        loanRepository.delete(persistedLoan);

        // Assert
        Loan deletedLoan = entityManager.find(Loan.class, persistedLoan.getId());
        assertThat(deletedLoan).isNull();
    }

    // --- Custom Query Test ---

    @Test
    void givenLoans_whenFindAllByReturned_thenReturnsCorrectList() {
        // Arrange
        entityManager.persist(activeLoan);   // returned = false
        entityManager.persist(returnedLoan); // returned = true

        // Act 1: Find Active Loans (returned = false)
        List<Loan> activeLoans = loanRepository.findAllByReturned(false);

        // Assert 1
        assertThat(activeLoans.get(2).getBook().getTitle()).isEqualTo("Spring Boot Action");

        // Act 2: Find Returned Loans (returned = true)
        List<Loan> historyLoans = loanRepository.findAllByReturned(true);

        // Assert 2
        assertThat(historyLoans.get(1).getBook().getTitle()).isEqualTo("Java Optimized");
    }
}