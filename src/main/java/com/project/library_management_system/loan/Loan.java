package com.project.library_management_system.loan;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.library_management_system.book.Book;
import com.project.library_management_system.user.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonProperty(access = Access.WRITE_ONLY)
    private Book book;

    @ManyToOne
    @JsonProperty(access = Access.WRITE_ONLY)
    private User user;
    

    private LocalDate loanDate;
    private LocalDate returnDate;

    @NotNull
    private boolean returned=false;
    

    public Loan(){

    }


    public Loan(Long id, Book book, User user, LocalDate loanDate, LocalDate returnDate, boolean returned) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.returned = returned;
    }

    public Loan(Loan loan) {
        this.id = loan.getId();
        this.book = loan.getBook();
        this.user = loan.getUser();
        this.loanDate = loan.getLoanDate();
        this.returnDate = loan.getReturnDate();
        this.returned = loan.isReturned();
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Book getBook() {
        return book;
    }


    public void setBook(Book book) {
        this.book = book;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public LocalDate getLoanDate() {
        return loanDate;
    }


    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }


    public LocalDate getReturnDate() {
        return returnDate;
    }


    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }


    public boolean isReturned() {
        return returned;
    }


    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    @JsonProperty("bookId") // This creates a "bookId": 200 field in the JSON
    public Long getBookId() {
        return book != null ? book.getId() : null;
    }

    @JsonProperty("userId") // This creates a "userId": 100 field in the JSON
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
        
    
}
