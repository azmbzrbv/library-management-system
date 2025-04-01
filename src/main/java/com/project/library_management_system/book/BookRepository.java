package com.project.library_management_system.book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository <Book, Long>  {

    public List<Book> findAllByAuthor(String author);

    public List<Book> findAllByTitle(String title);

    public List<Book> findAllByAvailable(boolean available);
}
