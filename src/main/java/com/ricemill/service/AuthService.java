package com.ricemill.service;

import com.ricemill.dto.AuthDto;
import com.ricemill.entity.RefreshToken;
import com.ricemill.entity.User;
import com.ricemill.entity.UserRole;
import com.ricemill.exception.BusinessException;
import com.ricemill.exception.ResourceNotFoundException;
import com.ricemill.repository.RefreshTokenRepository;
import com.ricemill.repository.UserRepository;
import com.ricemill.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.jwt.refresh-token-ttl}")
    private Long refreshTokenTtl;
    
    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );
        
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = createRefreshToken(user);
        
        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthDto.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .roles(user.getRoles())
                        .build())
                .build();
    }
    
    @Transactional
    public AuthDto.RefreshResponse refresh(AuthDto.RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));
        
        if (!refreshToken.isValid()) {
            throw new BusinessException("Refresh token expired or revoked");
        }
        
        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtUtil.generateToken(userDetails);
        
        return AuthDto.RefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .build();
    }
    
    @Transactional
    public void logout(AuthDto.RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));
        
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional
    public AuthDto.UserInfo register(AuthDto.RegisterRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        if (userRepository.existsByIdNumber(request.getIdNumber())) {
            throw new BusinessException("ID number already exists");
        }

        // Default to STAFF role for public registration
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.STAFF);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getUsername()) // Use username as fullName
                .idNumber(request.getIdNumber())
                .mobileNumber(request.getMobileNumber())
                .roles(roles)
                .build();
        user.setActive(true);

        user = userRepository.save(user);
        
        return AuthDto.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }
    
    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenTtl / 1000))
                .build();
        
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
