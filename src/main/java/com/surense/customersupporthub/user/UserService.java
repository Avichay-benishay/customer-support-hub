package com.surense.customersupporthub.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.surense.customersupporthub.exception.BadRequestException;
import com.surense.customersupporthub.exception.ConflictException;
import com.surense.customersupporthub.exception.NotFoundException;
import com.surense.customersupporthub.exception.UnauthorizedException;
import com.surense.customersupporthub.user.dto.CreateCustomerRequest;
import com.surense.customersupporthub.user.dto.UpdateProfileRequest;
import com.surense.customersupporthub.user.dto.UserResponse;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createCustomer(
            CreateCustomerRequest request,
            String currentUsername,
            String currentRole) {

        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        User agent = resolveAgentForCustomerCreation(request, currentUsername, currentRole);

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(Role.CUSTOMER);
        user.setAgent(agent);

        return toResponse(userRepository.save(user));
    }

    private User resolveAgentForCustomerCreation(
            CreateCustomerRequest request,
            String currentUsername,
            String currentRole) {

        if ("ADMIN".equals(currentRole)) {
            if (request.agentId() == null) {
                throw new BadRequestException("agentId is required when ADMIN creates a customer");
            }

            User agent = userRepository.findById(request.agentId())
                    .orElseThrow(() -> new NotFoundException("Agent not found"));

            if (agent.getRole() != Role.AGENT) {
                throw new BadRequestException("agentId must belong to an AGENT user");
            }

            return agent;
        }

        User agent = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        if (agent.getRole() != Role.AGENT) {
            throw new UnauthorizedException("Only agents can create customers");
        }

        return agent;
    }
    
    public List<UserResponse> getCustomers(String username, String role) {

        if ("ADMIN".equals(role)) {
            return userRepository.findByRole(Role.CUSTOMER)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        User agent = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        return userRepository.findByAgentId(agent.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        return toResponse(user);
    }

    public UserResponse updateMyProfile(
            String username,
            UpdateProfileRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        user.setName(request.name());
        user.setEmail(request.email());

        return toResponse(userRepository.save(user));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}