package com.example.backend.modules.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.modules.users.requests.BlacklistedTokenRequest;
import com.example.backend.modules.users.requests.LoginRequest;
import com.example.backend.modules.users.resources.LoginResource;
import com.example.backend.modules.users.services.impl.BlacklistedTokenService;
import com.example.backend.modules.users.services.interfaces.UserServiceInterface;
import com.example.backend.resources.ApiResource;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final UserServiceInterface userService;

    @Autowired
    private BlacklistedTokenService blacklistedTokenService;
 
    public AuthController(
        UserServiceInterface userService
    ){
        this.userService = userService;
    }
 
    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Object result = userService.authenticate(request);

        if (result instanceof LoginResource loginResource) {
            ApiResource<LoginResource> response = ApiResource.ok(loginResource, "SUCCESS");
            return ResponseEntity.ok(response);
        }

        if (result instanceof ApiResource errorResource) {
            return ResponseEntity.unprocessableEntity().body(errorResource);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Network error");
    }

   @PostMapping("blacklisted_token")
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlacklistedTokenRequest request) {
        try {
            Object result = blacklistedTokenService.create(request);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("Network error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            BlacklistedTokenRequest request = new BlacklistedTokenRequest();
            request.setToken(token);
            
            Object message = blacklistedTokenService.create(request);
            
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("Network error", HttpStatus.UNAUTHORIZED));
        }
    }
}
