package com.example.backend.modules.users.services.impl;
 
 import org.springframework.security.core.userdetails.UserDetails;
 import org.springframework.security.core.userdetails.UsernameNotFoundException;
 import org.springframework.stereotype.Service;
 
 import com.example.backend.modules.users.entities.User;
 import com.example.backend.modules.users.repositories.UserRepository;
 
 import org.springframework.security.core.userdetails.UserDetailsService;
 
 import lombok.RequiredArgsConstructor;
 
 import java.util.Collections;
 
 @RequiredArgsConstructor
 @Service
 public class CustomUserDetailsService implements UserDetailsService {
     private final UserRepository userRepository;
 
     @Override
     public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
         User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(
             () -> new UsernameNotFoundException("User not found!")
         );
 
         return new org.springframework.security.core.userdetails.User(
             user.getEmail(), 
             user.getPassword(), 
             Collections.emptyList()
         );
     }
 }