package com.project.library_management_system.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.library_management_system.loan.Loan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;

    @NotNull
    private boolean available = true;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)//cascasdeType.All means whatever happens with it will happend with loan
    @JsonIgnore
    private Loan loan;  //this column will not be here
                        // you are saying that it will be handled by the Loan.book
                        //there will be book_id column in the loand

    public Book(){
        
    }

    public Book(Long id, String title, String author, String isbn, boolean available, Loan loan) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = available;
        this.loan = loan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    
}
