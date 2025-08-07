package com.practice.demopractice.service.impl.user;

import com.practice.demopractice.dto.ResponseDTO;
import com.practice.demopractice.dto.UserRequestDTO;

public interface UserService {

    ResponseDTO createUser(UserRequestDTO dto);
    ResponseDTO getAllUsers();
    ResponseDTO getUserById(Long id);
    ResponseDTO updateUser(Long id, UserRequestDTO dto);
    ResponseDTO deleteUser(Long id);
}