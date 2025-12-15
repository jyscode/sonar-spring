package com.example.userapp.controller;

import com.example.userapp.domain.User;
import com.example.userapp.service.UserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.userapp.repository.UserRepository;
import com.example.userapp.config.RabbitMQConfig;
import com.example.userapp.dto.LogMessage;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public UserController(UserRepository userRepository, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    // 사용자 목록 조회 (GET /api/users)
    @GetMapping
    public List<User> listUsers(HttpServletRequest request) {
        List<User> users = userRepository.findAll();

         try {
            LogMessage log = new LogMessage();
            log.setAction("List Users");
            log.setEndpoint(request.getRequestURI());
            log.setTimestamp(Instant.now().toString());

            String json = objectMapper.writeValueAsString(log); // JSON으로 직렬화
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE, json);
        } catch (Exception e) {
            e.printStackTrace(); // 로깅만
        }
        return users;
    }

    // 사용자 등록 (POST /api/users)
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request) {
        User savedUser = userRepository.save(user);
        try {
            LogMessage log = new LogMessage();
            log.setAction("Add User");
            log.setEndpoint(request.getRequestURI());
            log.setTimestamp(Instant.now().toString());

            String json = objectMapper.writeValueAsString(log); // 직렬화
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE, json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}

