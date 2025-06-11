package com.azki.reservation.service;

import com.azki.reservation.dto.auth.SignupRequestDto;
import com.azki.reservation.exception.DuplicateUserException;
import com.azki.reservation.model.User;
import com.azki.reservation.repository.UserRepository;
import com.azki.reservation.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {


    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public String authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }

    public User signup(SignupRequestDto signupRequest) {
        if (userRepository.findByUsernameIgnoreCase(signupRequest.username()).isPresent()) {
            throw new DuplicateUserException("Username already exists");
        }
        if (userRepository.findByEmailIgnoreCase(signupRequest.email()).isPresent()) {
            throw new DuplicateUserException("Email already exists");
        }

        return userRepository.save(
                User.builder()
                        .username(signupRequest.username())
                        .email(signupRequest.email())
                        .password(passwordEncoder.encode(signupRequest.password()))
                        .build()
        );
    }
}
