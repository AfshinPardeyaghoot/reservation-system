package com.azki.reservation.service;

import com.azki.reservation.exception.NotFoundException;
import com.azki.reservation.model.User;
import com.azki.reservation.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
