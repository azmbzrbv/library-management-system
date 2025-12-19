package com.project.library_management_system.user;

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
import com.project.library_management_system.loan.Loan;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/users")
public class UserController {

    private static String error = "Users are not found";
    private UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @GetMapping
    public List<User> findAllUsers(){
        return this.userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id){
        Optional<User> optional = userRepository.findById(id);
        if(optional.isEmpty()){
            throw new NotFoundException(error);
        }

        return optional.get();
    }

    @GetMapping("/by-email")
    public User findByEmail(@RequestParam String email){
        Optional<User> optional=userRepository.findByEmail(email);
        if(optional.isEmpty()){
            throw new NotFoundException("User doesn't exists");
        }

        return optional.get();
    }

    @GetMapping("/by-name")
    public List<User> findAllByName(@RequestParam String name){
        List<User> users= userRepository.findAllByName(name);
        if(users.isEmpty()){
            throw new NotFoundException(error);
        }

        return users;
    }


    @GetMapping("/{id}/loans")
    public List<Loan> findLoans(@PathVariable Long id){
        Optional<User> optional = userRepository.findById(id);
        if(optional.isEmpty()){
            throw new NotFoundException(error);
        }

        return optional.get().getLoans();
    }

    @GetMapping("/approved")
    public List<User> findAllApproved(){
        List<User> users = userRepository.findAllByApproved(true);
        if(users.isEmpty()){
            throw new NotFoundException(error);
        }

        return users;
    }

    @GetMapping("/non-approved")
    public List<User> findAllNonApproved(){
        List<User> users = userRepository.findAllByApproved(false);
        if(users.isEmpty()){
            throw new NotFoundException(error);
        }

        return users;
    }


    @GetMapping("/by-role")
    public List<User> findAllByRole(@RequestParam Role role){
        List<User> users = userRepository.findAllByRole(role);
        if(users.isEmpty()){
            throw new NotFoundException(error);
        }

        return users;
    }

    
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create (@RequestBody @Valid User user){
        userRepository.save(user);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id){
        userRepository.deleteById(id);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value="/{id}", consumes = "application/json")
    void update(@PathVariable Long id, @RequestBody @Valid User updatedUser){
        User user=userRepository.findById(id)
            .map(existingUser ->{
                existingUser.setApproved(updatedUser.isApproved());
                existingUser.setName(updatedUser.getName());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setRole(updatedUser.getRole());
                existingUser.setLoans(updatedUser.getLoans());

                return existingUser;
            }) 
            .orElseThrow(()-> new NotFoundException(error));

            userRepository.save(user);
    }





}
