package com.example.backend.modules.users.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.modules.users.entities.User;
import com.example.backend.modules.users.repositories.UserRepository;
import com.example.backend.modules.users.requests.LoginRequest;
import com.example.backend.modules.users.resources.LoginResource;
import com.example.backend.modules.users.resources.UserResource;
import com.example.backend.modules.users.services.interfaces.UserServiceInterface;
import com.example.backend.resources.ApiResource;
import com.example.backend.services.BaseService;
import com.example.backend.services.JwtService;

@Service
public class UserService extends BaseService implements UserServiceInterface {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
     private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.defaultExpiration}")
     private Long defaultExpiration;

    @Override
    public Object authenticate(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BadCredentialsException("Incorrect email or password"));
                if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
                    throw new BadCredentialsException("Incorrect email or password");
                }
                UserResource userResource = UserResource.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .middleName(user.getMiddleName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .build();

            String token = jwtService.generateToken(user.getId(), user.getEmail(), defaultExpiration);
            
            return new LoginResource(token, userResource);

        } catch (BadCredentialsException e) {
            return ApiResource.error("AUTH_ERROR", e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}