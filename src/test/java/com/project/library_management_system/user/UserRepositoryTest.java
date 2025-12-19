package com.project.library_management_system.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Assuming User constructor: id, name, email, role, approved, loans
        // We use 'null' for ID so the database auto-generates it (IDENTITY strategy)
        user1 = new User(null, "Alice Smith", "alice@example.com", "password123",  Role.USER, true, new ArrayList<>());

        // user2 has different data for testing filters (False approval, ADMIN role)
        user2 = new User(null, "Bob Jones", "bob@example.com", "sampelPassword",  Role.ADMIN, false, new ArrayList<>());
    }

    // --- Standard CRUD Tests ---

    @Test
    void givenNewUser_whenSave_thenSuccess() {
        // Arrange
        User newUser = user1;

        // Act
        User insertedUser = userRepository.save(newUser);

        // Assert
        // Check that the Entity Manager can find the user we just saved via Repository
        assertThat(entityManager.find(User.class, insertedUser.getId())).isEqualTo(newUser);
    }

    @Test
    void givenUserCreated_whenUpdate_thenSuccess() {
        // Arrange
        entityManager.persist(user1);
        User userToUpdate = user1;
        userToUpdate.setName("Alice Updated");

        // Act
        userRepository.save(userToUpdate);

        // Assert
        assertThat(entityManager.find(User.class, userToUpdate.getId()).getName()).isEqualTo("Alice Updated");
    }

    @Test
    void givenUserCreated_whenFindById_thenSuccess() {
        // Arrange
        entityManager.persist(user1);

        // Act
        Optional<User> retrievedUser = userRepository.findById(user1.getId());

        // Assert
        assertThat(retrievedUser).contains(user1);
    }

    @Test
    void givenUserCreated_whenDelete_thenSuccess() {
        // Arrange
        entityManager.persist(user1);

        // Act
        userRepository.delete(user1);

        // Assert
        assertThat(entityManager.find(User.class, user1.getId())).isNull();
    }

    // --- Custom Query Methods Tests ---

    @Test
    void givenUserCreated_whenFindByEmail_thenSuccess() {
        // Arrange
        entityManager.persist(user1);
        entityManager.persist(user2);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("alice@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user1);
    }

    @Test
    void givenUserCreated_whenFindAllByName_thenSuccess() {
        // Arrange
        entityManager.persist(user1); // Alice Smith
        entityManager.persist(user2); // Bob Jones

        // Act
        List<User> foundUsers = userRepository.findAllByName("Bob Jones");

        // Assert
        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers).contains(user2);
    }

    @Test
    void givenUserCreated_whenFindAllByApproved_thenSuccess() {
        // Arrange
        entityManager.persist(user1); // Approved = true
        entityManager.persist(user2); // Approved = false

        // Act - Check for approved users
        List<User> approvedUsers = userRepository.findAllByApproved(true);

        // Assert
        assertThat(approvedUsers).contains(user1);
        assertThat(approvedUsers).doesNotContain(user2);
    }

    @Test
    void givenUserCreated_whenFindAllByRole_thenSuccess() {
        // Arrange
        entityManager.persist(user1); // Role.USER
        entityManager.persist(user2); // Role.ADMIN

        // Act - Find ADMINs
        List<User> adminUsers = userRepository.findAllByRole(Role.ADMIN);

        // Assert
        assertThat(adminUsers).contains(user2);
        assertThat(adminUsers).doesNotContain(user1);
    }
}