package com.project.library_management_system.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
        private final CustomUserDetailsService userDetailsService;

        public SecurityConfig(CustomUserDetailsService userDetailsService){
            this.userDetailsService=userDetailsService;
        }


        @Bean
        public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
            http.csrf(csrf->csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->auth
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html"
                            ).permitAll()
                            .requestMatchers("/auth/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/api/users/**").hasRole("ADMIN")
                            .requestMatchers("/api/loans/**").hasAnyRole("ADMIN", "USER")
                            .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .userDetailsService(userDetailsService)

            .sessionManagement(session -> 
                          session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                          
            .headers(headers -> headers.frameOptions().sameOrigin());

            return http.build();
        }



     // tells how to extract the user roles
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String scope = jwt.getClaimAsString("scope");
            return scope == null ? List.of() :
                Arrays.stream(scope.split(" ", -1))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
   }



     @Bean
    public KeyPair keyPair(){
        try{
            var keypairGenerator = KeyPairGenerator.getInstance("RSA");
            keypairGenerator.initialize(2048);//the bigger the key size the higher the security
            return keypairGenerator.generateKeyPair();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


    @Bean 
    RSAKey rsaKey(KeyPair keyPair){
        return new RSAKey.Builder((RSAPublicKey)keyPair().getPublic())
                  .privateKey(keyPair().getPrivate())
                  .keyID(UUID.randomUUID().toString())
                  .build();
    }

    @Bean
    JWKSource<SecurityContext> jwkSource(RSAKey rsaKey){
        var jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, context)->jwkSelector.select(jwkSet);  
    }


    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException{
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey())
                            .build();
    }


    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource){
        return new NimbusJwtEncoder(jwkSource);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //default costfactor is 10
    }

   

}
