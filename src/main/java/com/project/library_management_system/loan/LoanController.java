package com.project.library_management_system.loan;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.library_management_system.book.Book;
import com.project.library_management_system.book.BookRepository;
import com.project.library_management_system.exception.NotFoundException;
import com.project.library_management_system.user.User;
import com.project.library_management_system.user.UserRepository;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private BookRepository bookRepository;
    private UserRepository userRepository;
    private LoanRepository loanRepository;

    public LoanController(BookRepository bookRepository, UserRepository userRepository, LoanRepository loanRepository){
        this.bookRepository=bookRepository;
        this.userRepository=userRepository;
        this.loanRepository=loanRepository;
    }


    @GetMapping
    public List<Loan> findAllLoans(){
        List<Loan> loans = loanRepository.findAll();
        if(loans.isEmpty()){
            throw new NotFoundException("Loans are not found");
        }

        return loans;
    }

    @GetMapping("/{id}")
    public Loan findById(@PathVariable Long id){
        Optional<Loan> optional = loanRepository.findById(id);
        if(optional.isEmpty()){
            throw new NotFoundException("Loans doesn't exists");
        }

        return optional.get();
    }

    @GetMapping("/by-userId")
    public List<Loan> findByUserId(@RequestParam Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new NotFoundException("User doesn't exists");
        }
        return user.get().getLoans();
    }

    @GetMapping("by-userEmail")
    public List<Loan> findByUserEmail(@RequestParam String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("User doesn't exist");
        }
        return user.get().getLoans();
    }


    @GetMapping("/by-bookId")
    public Loan findByBookId(@RequestParam Long id){
        Optional<Book> optional = bookRepository.findById(id);
        if(optional.isEmpty()){
            throw new NotFoundException("Book does not exist in library");
        }
        return optional.get().getLoan();
    }

    @GetMapping("/returned")
    public List<Loan> getReturnedLoans() {
           return loanRepository.findAllByReturned(true);
    }

    @GetMapping("/active")
    public List<Loan> getActiveLoans() {
           return loanRepository.findAllByReturned(false);
    }

    
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createLoan(@RequestParam Long userId, @RequestParam Long bookId){
        User user = userRepository.findById(userId)
                   .orElseThrow(() -> new NotFoundException("User not found"));
        Book book = bookRepository.findById(bookId)
                   .orElseThrow(() -> new NotFoundException("Book not found"));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setReturned(false);

        loanRepository.save(loan);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@RequestBody @Valid Loan updatedLoan, @PathVariable Long id){
        Loan loan = loanRepository.findById(id)
             .map(existingLoan->{
                existingLoan.setBook(updatedLoan.getBook());
                existingLoan.setLoanDate(updatedLoan.getLoanDate());
                existingLoan.setReturnDate(updatedLoan.getReturnDate());
                existingLoan.setReturned(updatedLoan.isReturned());
                existingLoan.setUser(existingLoan.getUser());

                return existingLoan;
             }).orElseThrow(()-> new NotFoundException("The loan does not exist"));

        loanRepository.save(loan);
    }  
    

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
         loanRepository.deleteById(id);
    }


}
