package com.example.expense_tracker_api.service;

import com.example.expense_tracker_api.dto.AuthResponse;
import com.example.expense_tracker_api.dto.LoginRequest;
import com.example.expense_tracker_api.dto.RegisterRequest;
import com.example.expense_tracker_api.model.User;
import com.example.expense_tracker_api.respository.UserRepository;
import com.example.expense_tracker_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final JwtService jwtService ;
    private final AuthenticationManager authenticationManager ;

    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("User already exists") ;
        }
        User user = new User() ;
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user) ;

        String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build());
        return new AuthResponse(token) ;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username() , request.password())
        );
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found")) ;

        String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build()) ;
        return new AuthResponse(token) ;
    }
}
