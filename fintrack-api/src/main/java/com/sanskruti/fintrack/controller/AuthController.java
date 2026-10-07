package com.sanskruti.fintrack.controller;

import com.sanskruti.fintrack.dto.AuthRequest;
import com.sanskruti.fintrack.dto.AuthResponse;
import com.sanskruti.fintrack.model.User;
import com.sanskruti.fintrack.repository.UserRepository;
import com.sanskruti.fintrack.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        users.save(new User(req.username(), encoder.encode(req.password())));
        return new AuthResponse(jwt.generateToken(req.username()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest req) {
        User user = users.findByUsername(req.username())
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return new AuthResponse(jwt.generateToken(user.getUsername()));
    }
}
