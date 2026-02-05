package com.security.test.api.service;

import com.security.test.api.dto.request.LoginRequestDTO;
import com.security.test.api.dto.request.RegisterRequestDTO;
import com.security.test.api.dto.response.RegisterResponseDTO;
import com.security.test.api.dto.response.LoginResponseDTO;
import com.security.test.api.entity.User;
import com.security.test.api.enums.UserRole;
import com.security.test.api.repository.UserRepository;
import com.security.test.api.exception.UserAlreadyExistsException;
import com.security.test.api.utils.TokenProviderUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProviderUtil tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenProviderUtil tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = tokenProvider.generateToken((User) auth.getPrincipal());

        return new LoginResponseDTO(token);
    }

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        isUserExists(request);

        String encryptedPassword = passwordEncoder.encode(request.password());
        User newUser = new User(request.login(), encryptedPassword, UserRole.USER);

        userRepository.save(newUser);

        return new RegisterResponseDTO(newUser.getId(), newUser.getUsername());
    }

    private void isUserExists(RegisterRequestDTO data) {
        if (userRepository.existsByLogin(data.login())) {
            throw new UserAlreadyExistsException("User already exists");
        }
    }
}