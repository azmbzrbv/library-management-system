package com.project.library_management_system.loan;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.library_management_system.book.Book;
import com.project.library_management_system.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;


@Entity
public class Loan {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JsonManagedReference
    private Book book;

    @ManyToOne
    @JsonManagedReference //will be shown in the json
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
        
    
}
