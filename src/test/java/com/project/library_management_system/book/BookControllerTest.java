package com.project.library_management_system.book;



import com.project.library_management_system.security.CustomUserDetailsService;
import com.project.library_management_system.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(BookController.class)
/// importing securityConfig for checking the security together
@Import(SecurityConfig.class)
public class BookControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookRepository bookRepository;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;  ///importing this for the security

    private Book mockBook;
    private List<Book> mockBookList;

    @BeforeEach
    void setUp() {
        mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("The Great Gatsby");
        mockBook.setAuthor("F. Scott Fitzgerald");
        mockBook.setAvailable(true);

        mockBookList = List.of(mockBook, new Book(2L, "1984", "George Orwell", "222", true, null));
    }
    private String bookToJson(Book book) {
        return "{\"title\":\"" + book.getTitle() + "\", \"author\":\"" + book.getAuthor() + "\", \"isbn\":\"" + book.getIsbn() + "\", \"available\":" + book.isAvailable() + "}";
    }

    // --- 1. Test GET /api/books (Find All) ---
    @Test
    @WithMockUser(roles = "USER")
    // Simulate an authenticated user (required for secured endpoints)
    void findAllBooks_shouldReturnListOfBooks() throws Exception {
        // ARRANGE: Stub the repository to return the mock list
        when(bookRepository.findAll()).thenReturn(mockBookList);

        // ACT & ASSERT
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("The Great Gatsby")));

        // VERIFY: Ensure the controller called the repository method exactly once
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllBooks_shouldReturnEmptyList() throws Exception{
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findById_shouldReturnBookWhenFound() throws Exception{
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        mockMvc.perform(get("/api/books/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Great Gatsby")));
    }

    @Test
    @WithMockUser (roles = "USER")
    void findById_shouldReturnNotFoundWhenBookIsMissing() throws Exception{
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser (roles = "ADMIN")
    void create_shouldCreateNewBook() throws Exception{
        Book newBook = new Book(null, "New Title", "New Author", "123", true, null);
        String newBookJson = bookToJson(newBook);

        mockMvc.perform(post("/api/books")
                        .with(csrf()) // Required for non-GET requests in Spring Security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isCreated());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteById_shouldReturnNoContentStatus() throws Exception{
        final Long bookIdToDelete = 1l;

        mockMvc.perform(delete("/api/books/{id}", bookIdToDelete)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bookRepository, times(1)).deleteById(bookIdToDelete);
    }


    @Test
    @WithMockUser(roles = "USER")
    void deleteById_shouldReturnForbidden() throws Exception{
        final Long bookIdToDelete = 1L;

        mockMvc.perform(delete("/api/books/{id}", bookIdToDelete)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bookRepository, never()).deleteById(anyLong());
    }

}
