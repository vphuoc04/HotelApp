package com.example.backend.modules.users.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.modules.users.entities.User;
import com.example.backend.modules.users.repositories.UserRepository;
import com.example.backend.modules.users.resources.UserResource;
import com.example.backend.modules.users.services.interfaces.UserServiceInterface;
import com.example.backend.resources.ApiResource;
import com.example.backend.services.JwtService;

import com.example.backend.modules.users.requests.User.StoreRequest;
import com.example.backend.modules.users.requests.User.UpdateRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1")
public class UserController {
    private final UserServiceInterface userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(
        UserServiceInterface userService,
        JwtService jwtService
    ){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("me")
    public ResponseEntity<?> user() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Not found!"));

        UserResource userResource = UserResource.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .middleName(user.getMiddleName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .build();

        return ResponseEntity.ok(userResource);
    }

    @PostMapping("/add_user")
    public ResponseEntity<?> add(@Valid @RequestBody StoreRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            String userId = jwtService.getUserIdFromJwt(token);

            Long addedBy = Long.valueOf(userId);

            String encodedPassword = passwordEncoder.encode(request.getPassword());
            request.setPassword(encodedPassword);

            User user = userService.add(request, addedBy);

            UserResource userResource = UserResource.builder()
                .id(user.getId())
                .catalogueId(user.getCatalogueId())
                .addedBy(user.getAddedBy())
                .editedBy(user.getEditedBy())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .phone(user.getPhone())
                .build();

            ApiResource<UserResource> response = ApiResource.ok(userResource, "New user added successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("Network error", HttpStatus.UNAUTHORIZED));
        } 
    }

    @PutMapping("/edit_user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            String userId = jwtService.getUserIdFromJwt(token);

            Long updatedBy = Long.valueOf(userId);

            String encodedPassword = passwordEncoder.encode(request.getPassword());
            request.setPassword(encodedPassword);

            User user = userService.edit(id, request, updatedBy);
            
            UserResource userResource = UserResource.builder()
                .id(user.getId())
                .addedBy(user.getAddedBy())
                .editedBy(user.getEditedBy())
                .catalogueId(user.getCatalogueId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
                
            ApiResource<UserResource> response = ApiResource.ok(userResource, "User updated successfully");

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error("INTERNAL_SERVER_ERROR", "Error", HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }   
    }

    @GetMapping("/user_belong_cataloge/{catalogueId}")
    public ResponseEntity<?> getUsersByCatalogue(@PathVariable Long catalogueId, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();

        Page<User> users = userService.paginate(catalogueId, parameters);

        Page<UserResource> userResources = users.map(user -> 
            UserResource.builder()
                .id(user.getId())
                .catalogueId(user.getCatalogueId())
                .addedBy(user.getAddedBy())
                .editedBy(user.getEditedBy())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(user.getPassword())
                .build()
        );

        ApiResource<Page<UserResource>> response = ApiResource.ok(userResources, "List user of user catalogue fetched successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean deleted = userService.delete(id);

            if (deleted) {
                return ResponseEntity.ok(
                    ApiResource.message("User deleted successfully", HttpStatus.OK)  
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResource.error("NOT_FOUND", "Error", HttpStatus.NOT_FOUND)
                );
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error("INTERNAL_SERVER_ERROR", "Error", HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }   
    }
}
