package com.practice.demopractice.service.impl.user;

import com.practice.demopractice.Repository.UserRepository;
import com.practice.demopractice.dto.ResponseDTO;
import com.practice.demopractice.dto.UserRequestDTO;
import com.practice.demopractice.entity.User;
import com.practice.demopractice.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseDTO createUser(UserRequestDTO dto) {
        Optional<User> userData = userRepo.findByUsername(dto.getUsername());
        if (userData.isPresent()) {

            return ResponseDTO.builder()
                    .status("Fail")
                    .statusCode(400)
                    .message("Username already exists")
                    .build();

        }
        try {

            UserRole role = UserRole.valueOf(dto.getRole().toUpperCase());

            User user = User.builder()// avoid overload constructor
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role(role)
                    .build();

            User savedUser = userRepo.save(user);

            return ResponseDTO.builder()
                    .status("success")
                    .statusCode(201)
                    .message("User created successfully")
                    .data(convertUserToMap(savedUser))
                    .build();

        } catch (ArithmeticException e) {
            System.out.println("Exception: "+e.getMessage()+" path : ");
            for (StackTraceElement el : e.getStackTrace()) {
                System.out.println(el);
            }
            return ResponseDTO.builder()
                    .status("fail")
                    .statusCode(400)
                    .message(e.getMessage())
                    .build();

        }catch (NullPointerException e) {
            e.printStackTrace();
            ResponseDTO fail = ResponseDTO.builder()
                    .status("fail")
                    .statusCode(400)
                    .message(e.getMessage())
                    .build();
            return fail;

        }catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseDTO.builder()
                    .status("fail")
                    .statusCode(400)
                    .message("Invalid role specified. Must be ADMIN or USER")
                    .build();

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.builder()
                    .status("fail")
                    .statusCode(400)
                    .message(e.getMessage())
                    .build();

        }finally {
            System.out.println("Before return");
        }


    }

    @Override
    public ResponseDTO getAllUsers() {
        List<User> users = userRepo.findAll();

        if (users.isEmpty()) {
            return ResponseDTO.builder()
                    .status("success")
                    .statusCode(200)
                    .message("No users found")
                    .build();
        }

        List<Map<String, Object>> userList = users.stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());

        return ResponseDTO.builder()
                .status("success")
                .statusCode(200)
                .data(userList)
                .message("Users retrieved successfully")
                .build();
    }

    @Override
    public ResponseDTO getUserById(Long id) {
        Optional<User> userOptional = userRepo.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseDTO.builder()
                    .status("fail")
                    .statusCode(404)
                    .message("User not found with id: " + id)
                    .build();
        }

        return ResponseDTO.builder()
                .status("success")
                .statusCode(200)
                .data(convertUserToMap(userOptional.get()))
                .message("User retrieved successfully")
                .build();
    }

    @Override
    public ResponseDTO updateUser(Long id, UserRequestDTO dto) {
        Optional<User> userOptional = userRepo.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseDTO.builder()
                    .status("fail")
                    .statusCode(404)
                    .message("User not found with id: " + id)
                    .build();
        }

        User user = userOptional.get();

        // Username update check
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            if (userRepo.findByUsername(dto.getUsername()).isPresent() &&
                    !user.getUsername().equals(dto.getUsername())) {
                return ResponseDTO.builder()
                        .status("fail")
                        .statusCode(400)
                        .message("Username already exists")
                        .build();
            }
            user.setUsername(dto.getUsername());
        }

        // Password update
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Role update with validation
        if (dto.getRole() != null && !dto.getRole().isEmpty()) {
            try {
                user.setRole(UserRole.valueOf(dto.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseDTO.builder()
                        .status("fail")
                        .statusCode(400)
                        .message("Invalid role specified")
                        .build();
            }
        }

        User updatedUser = userRepo.save(user);

        return ResponseDTO.builder()
                .status("success")
                .statusCode(200)
                .data(convertUserToMap(updatedUser))
                .message("User updated successfully")
                .build();
    }

    @Override
    public ResponseDTO deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            return ResponseDTO.builder()
                    .status("fail")
                    .statusCode(404)
                    .message("User not found with id: " + id)
                    .build();
        }

        userRepo.deleteById(id);

        return ResponseDTO.builder()
                .status("success")
                .statusCode(200)
                .message("User deleted successfully")
                .build();
    }

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("role", user.getRole().name());
        return userMap;
    }
}