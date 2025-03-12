package com.example.backend.modules.users.services.interfaces;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.backend.modules.users.entities.User;
import com.example.backend.modules.users.requests.LoginRequest;
import com.example.backend.modules.users.requests.User.StoreRequest;
import com.example.backend.modules.users.requests.User.UpdateRequest;

public interface UserServiceInterface {
    Object authenticate(LoginRequest request);  
    User add(StoreRequest request, Long addedBy);
    User edit(Long id, UpdateRequest request, Long editedBy);
    Page<User> paginate(Long catalogueId, Map<String, String[]> parameters);
    boolean delete(Long id);
} 
