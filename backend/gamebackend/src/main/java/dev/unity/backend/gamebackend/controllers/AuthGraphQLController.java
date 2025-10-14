package dev.unity.backend.gamebackend.controllers;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import dev.unity.backend.gamebackend.repository.UserRepository;
import dev.unity.backend.gamebackend.entity.User;

@Controller
@RequiredArgsConstructor
public class AuthGraphQLController {

    private final UserRepository userRepository;

    @QueryMapping
    public Iterable<User> users() {
        return userRepository.findAll();
    }

    @QueryMapping
    public User userByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @MutationMapping
    public User register(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return userRepository.save(user);
    }

    @MutationMapping
    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
}
