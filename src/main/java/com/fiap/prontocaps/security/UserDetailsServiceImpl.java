package com.fiap.prontocaps.security;

import com.fiap.prontocaps.user.UserEntity;
import com.fiap.prontocaps.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().name())
                .disabled(!user.isEnabled())
                .build();
    }
}