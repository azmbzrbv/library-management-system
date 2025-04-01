package com.project.library_management_system.book;

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

import com.project.library_management_system.exception.NotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {


    private static String error="Books Are Not Found";
    private BookRepository bookRepository;

    public BookController(BookRepository bookRepository){
        this.bookRepository=bookRepository;
    }

    @GetMapping
    public List<Book> findAllBooks(){
        return this.bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book findById(@PathVariable Long id){
        Optional<Book> optional = bookRepository.findById(id);
        if(optional.isEmpty()){
            throw new NotFoundException(error);
        }
        return optional.get();
    }

    @GetMapping("/by-author")
    public List<Book> findAllByAuthor(@RequestParam String author){
        List<Book> list = bookRepository.findAllByAuthor(author);
        if(list.isEmpty()){
            throw new NotFoundException(error);
        }
        return list;
    }


    @GetMapping("/by-title")
    public List<Book> findAllByTitle(@RequestParam String title){
        List<Book> list = bookRepository.findAllByTitle(title);
        if(list.isEmpty()){
            throw new NotFoundException(error);
        }
        return list;
    }


    @GetMapping("/available")
    public List<Book> findAllAvailable(){
        List<Book> list = bookRepository.findAllByAvailable(true);
        if(list.isEmpty()){
            throw new NotFoundException(error);
        }
        return list;
    }

    @GetMapping("/non-available")
    public List<Book> findAllNonAvailable(){
        List<Book> list = bookRepository.findAllByAvailable(false);
        if(list.isEmpty()){
            throw new NotFoundException(error);
        }
        return list;
    }

    
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    void create(@RequestBody @Valid Book book){
        bookRepository.save(book);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id){
        bookRepository.deleteById(id);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@PathVariable Long id, @RequestBody @Valid Book updatedBook){
       Book book = bookRepository.findById(id)
        .map(existingBook -> {                       //map will take the current value of the optional
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setAvailable(updatedBook.isAvailable());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setLoan(updatedBook.getLoan());
            
            return existingBook;
            
        })
        .orElseThrow(()-> new NotFoundException(error));

        bookRepository.save(book);   //it will replace the current book with same id in the database. 
    }


}
