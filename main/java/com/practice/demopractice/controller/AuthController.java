package com.practice.demopractice.controller;

import com.practice.demopractice.dto.ResponseDTO;
import com.practice.demopractice.dto.UserRequestDTO;
import com.practice.demopractice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
     AuthenticationManager authenticationManager;

    @Autowired
     JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDTO dto) {
        System.out.println("Login attempt for: " + dto.getUsername());

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );

            System.out.println("Authentication successful: " + auth.getName());

            // Generate token after successful authentication
            String token = jwtUtil.generateToken(dto.getUsername());

            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .statusCode(200)
                    .message("Login successful")
                    .data(Map.of("token", token))
                    .build());

        } catch (BadCredentialsException e) {
            System.err.println("BAD CREDENTIALS: " + dto.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseDTO.builder()
                            .status("error")
                            .statusCode(401)
                            .message("Invalid credentials")
                            .build()
            );
        } catch (Exception e) {
            System.err.println("LOGIN ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDTO.builder()
                            .status("error")
                            .statusCode(403)
                            .message("Access denied: " + e.getMessage())
                            .build()
            );
        }
    }

}