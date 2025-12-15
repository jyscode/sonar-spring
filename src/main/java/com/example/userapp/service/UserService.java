package com.example.userapp.service;

import com.example.userapp.domain.User;
import com.example.userapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void save(User user) {
        repo.save(user);
    }

    public List<User> findAll() {
        return repo.findAll();
    }
}
