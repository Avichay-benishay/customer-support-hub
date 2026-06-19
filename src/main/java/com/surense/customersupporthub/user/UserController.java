package com.surense.customersupporthub.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.surense.customersupporthub.user.dto.CreateCustomerRequest;
import com.surense.customersupporthub.user.dto.UpdateProfileRequest;
import com.surense.customersupporthub.user.dto.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return userService.createCustomer(
                request,
                jwt.getSubject(),
                jwt.getClaimAsString("role")
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public List<UserResponse> getCustomers(
            @AuthenticationPrincipal Jwt jwt) {

        return userService.getCustomers(
                jwt.getSubject(),
                jwt.getClaimAsString("role")
        );
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER','AGENT','ADMIN')")
    public UserResponse getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        return userService.getMyProfile(jwt.getSubject());
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER','AGENT','ADMIN')")
    public UserResponse updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return userService.updateMyProfile(jwt.getSubject(), request);
    }
}