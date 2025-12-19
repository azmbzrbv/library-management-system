package com.project.library_management_system.loan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.library_management_system.book.Book;
import com.project.library_management_system.book.BookRepository;
import com.project.library_management_system.security.CustomUserDetailsService;
import com.project.library_management_system.security.SecurityConfig;
import com.project.library_management_system.user.User;
import com.project.library_management_system.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@Import(SecurityConfig.class)
public class LoanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LoanRepository loanRepository;

    @MockitoBean
    BookRepository bookRepository;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    private Loan mockLoan;
    private User mockUser;
    private Book mockBook;
    private List<Loan> mockLoanList;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(100L);
        mockUser.setEmail("alice@example.com"); // Matches the username we will test with
        mockUser.setName("Alice");

        mockBook = new Book();
        mockBook.setId(200L);
        mockBook.setTitle("Spring Boot in Action");

        mockLoan = new Loan();
        mockLoan.setId(1L);
        mockLoan.setBook(mockBook);
        mockLoan.setUser(mockUser);
        mockLoan.setLoanDate(LocalDate.now());
        mockLoan.setReturnDate(LocalDate.now().plusDays(14));
        mockLoan.setReturned(false);

        mockLoanList = List.of(mockLoan);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllLoans_shouldReturnList_whenAdmin() throws Exception{
        when(loanRepository.findAll()).thenReturn(mockLoanList);

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllLoans_shouldReturn403_whenUser() throws Exception{
        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN") // Admin can see ANY loan
    void findById_shouldReturnLoan_WhenAdmin() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(mockLoan));

        mockMvc.perform(get("/api/loans/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void findById_shouldReturnLoan_WhenUserIsOwner() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(mockLoan));

        mockMvc.perform(get("/api/loans/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "bob@example.com", roles = "USER")
    void findById_shouldReturn403_WhenUserIsNotOwner() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(mockLoan));

        mockMvc.perform(get("/api/loans/{id}", 1L))
                .andExpect(status().isForbidden()); // Security check blocks response
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createLoan_shouldReturnCreated() throws Exception {
        // Mock dependencies finding the user and book
        when(userRepository.findById(100L)).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(200L)).thenReturn(Optional.of(mockBook));

        mockMvc.perform(post("/api/loans")
                        .with(csrf())
                        .param("userId", "100")
                        .param("bookId", "200"))
                .andExpect(status().isCreated());

        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnNoContent() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(mockLoan));

        Loan updatedInfo = new Loan(mockLoan);
        updatedInfo.setReturned(true); // Changing status
        updatedInfo.setReturnDate(LocalDate.now());

        mockMvc.perform(put("/api/loans/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNoContent());

        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/loans/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(loanRepository, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByUserId_shouldReturnLoans() throws Exception {
        // You need to set the loans list on the mockUser for this to work
        mockUser.setLoans(mockLoanList);

        when(userRepository.findById(100L)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/loans/by-userId")
                        .param("id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
