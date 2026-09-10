package com.screenings.cervical_screenings.Controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.screenings.cervical_screenings.Entitys.User;
import com.screenings.cervical_screenings.Services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role
    ) {
        User.Role userRole = User.Role.valueOf(role.toUpperCase()); // NURSE, SPECIALIST, ADMIN
        User user = userService.createUser(username, password, userRole);

        Map<String, Object> resp = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name());

        return ResponseEntity.ok(resp);
    }
    
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails){
        Map<String, Object> resp = Map.of(
                "username", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities()
        );
        return ResponseEntity.ok(resp);
    }

 
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails){
       
        Map<String, Object> resp = Map.of(
                "requestedBy", userDetails.getUsername(),
                "targetUserId", id.toString());
        return ResponseEntity.ok(resp);
    }
}