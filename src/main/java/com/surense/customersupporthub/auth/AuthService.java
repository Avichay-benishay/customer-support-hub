package com.surense.customersupporthub.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.surense.customersupporthub.user.User;
import com.surense.customersupporthub.user.UserRepository;

import com.surense.customersupporthub.exception.UnauthorizedException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
        		.orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
        	throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}