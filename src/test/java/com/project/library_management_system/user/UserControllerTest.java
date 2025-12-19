package com.project.library_management_system.user;

import com.project.library_management_system.security.CustomUserDetailsService;
import com.project.library_management_system.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserRepository  userRepository;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ObjectMapper objectMapper;

    private User mockUser;
    private List<User> mockUserList;

    @BeforeEach
    void setUp(){
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Johny Cage");
        mockUser.setEmail("admin@example.com");
        mockUser.setRole(Role.ADMIN);
        mockUser.setApproved(false);
        mockUser.setLoans(new ArrayList<>());

        User mockUser2 = new User();
        mockUser2.setId(2L);
        mockUser2.setName("Bob Jones");
        mockUser2.setEmail("bob@example.com");
        mockUser2.setRole(Role.USER);
        mockUser2.setApproved(false);

        mockUserList = Arrays.asList(mockUser, mockUser2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllUsers_shouldReturnList() throws Exception{
        when(userRepository.findAll()).thenReturn(mockUserList);

        mockMvc.perform(get("/api/users")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Johny Cage")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllUsers_shouldReturnForbidden() throws Exception{

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturnUser_WhenFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("admin@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturn404_WhenNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByEmail_shouldReturnUser() throws Exception {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/by-email")
                        .param("email", "admin@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Johny Cage")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllByRole_shouldReturnUsers() throws Exception {
        // Assuming you fixed the infinite recursion bug in Controller!
        when(userRepository.findAllByRole(Role.USER)).thenReturn(List.of(mockUserList.get(1)));

        mockMvc.perform(get("/api/users/by-role")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].role", is("USER")));
    }

    // --- 2. POST / CREATE Request ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreated() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUser)))
                .andExpect(status().isCreated());

        // Verify that the repository save method was actually called
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- 3. PUT / UPDATE Request ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnNoContent_WhenUserExists() throws Exception {
        // Arrange
        User updatedInfo = new User(mockUser);
        updatedInfo.setName("Johny Updated");
        updatedInfo.setEmail("johny_new@example.com");
        updatedInfo.setApproved(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNoContent());

        // Verify save was called
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturn404_WhenUserMissing() throws Exception {
        User updatedInfo = new User(mockUser);
        updatedInfo.setName("New Name");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/{id}", 99L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNotFound());
    }

    // --- 4. DELETE Request ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteById_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).deleteById(1L);
    }



}