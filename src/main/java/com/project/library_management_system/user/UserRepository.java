package com.project.library_management_system.user;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAllByName(String name);

    List<User> findAllByApproved(boolean approved);

    List<User> findAllByRole(Role role);
}
