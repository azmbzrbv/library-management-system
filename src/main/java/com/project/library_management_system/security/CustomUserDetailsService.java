package com.project.library_management_system.security;

import java.util.Collections;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.library_management_system.user.User;
import com.project.library_management_system.user.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    //this function is called when user is trying to login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
        .orElseThrow(()->new UsernameNotFoundException("User not found"));

        if(!user.isApproved()){
            throw new DisabledException("User is not approved yet");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // already hashed
                Collections.singletonList(authority)
        );

    }


    
    
}
