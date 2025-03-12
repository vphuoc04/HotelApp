package com.example.backend.modules.users.services.interfaces;

import com.example.backend.modules.users.requests.LoginRequest;

public interface UserServiceInterface {
    Object authenticate(LoginRequest request);  
} 
