package com.chandan.userauthservice.services;

import com.chandan.userauthservice.models.User;
import com.chandan.userauthservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).get();
    }

    public User getUserById(Long uid) {
        return userRepository.findUserById(uid).get();
    }
}
