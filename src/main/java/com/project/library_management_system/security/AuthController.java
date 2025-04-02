package com.project.library_management_system.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.library_management_system.user.Role;
import com.project.library_management_system.user.User;
import com.project.library_management_system.user.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;
    private UserRepository userRepository;
    private PasswordEncoder encoder;


    public AuthController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, UserRepository userRepository, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userRepository=userRepository;
        this.encoder=encoder;
    }

    //login logic
    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request){
        //this call the method of the usercredentials
        Authentication authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(
                    request.getEmail(), 
                    request.getPassword()
             )
        );

        String token = createToken(authentication);
        return new JwtResponse(token);
        
    }

    private String createToken(Authentication authentication){
        var claims=  JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(1800))
                        .subject(authentication.getName())
                        .claim("scope", createScope(authentication))
                        .build();

        JwtEncoderParameters paramters = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(paramters).getTokenValue();
    }

    //getting the roles
    private String createScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                   .map(a-> a.getAuthority())
                   .collect(Collectors.joining(" "));
    }




    
    //register logic

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setApproved(false);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered. Awaiting approval.");
    }


    @GetMapping("/test")
    public String test(){
        return "test";
    }

    
}
