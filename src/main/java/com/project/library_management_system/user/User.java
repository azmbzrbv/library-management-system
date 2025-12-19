package com.project.library_management_system.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.library_management_system.loan.Loan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name="users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(unique = true)
    @Email
    private String email;
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)//will store the role as string
    private Role role;

    @NotNull
    private boolean approved=false;

    @OneToMany(mappedBy = "user")  //user field of Loand is owning side (loan class contains the user)
    @JsonIgnore   // willl not be shown in the json
    private List<Loan> loans;

    public User(){
      
    }

    public User(Long id, String name, String email, String password, Role role, boolean approved, List<Loan> loans) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.approved = approved;
        this.loans = loans;
    }

    public User(User source) {
        if (source != null) {
            this.id = source.getId();
            this.name = source.getName();
            this.email = source.getEmail();
            this.role = source.getRole();
            this.approved = source.isApproved();
            this.password = source.getPassword();
            // Note: This is a "Shallow Copy" of the list.
            // If you modify the list in the new user, it modifies the old one too.
            // If you want a "Deep Copy" (completely separate list), use:
            this.loans = source.getLoans() != null ? new ArrayList<>(source.getLoans()) : null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    

}
