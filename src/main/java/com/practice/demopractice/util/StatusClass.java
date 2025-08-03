package com.practice.demopractice.util;

import com.practice.demopractice.dto.ResponseDTO;
import org.springframework.http.HttpStatus;

public class StatusClass {

    public static ResponseDTO success(String message, HttpStatus status) {
        return ResponseDTO.builder()
                .status("success")
                .statusCode(status.value())
                .message(message)
                .build();
    }

    public static ResponseDTO success(String message, HttpStatus status, Object data) {
        return ResponseDTO.builder()
                .status("success")
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }

    public static ResponseDTO error(String message, HttpStatus status) {
        return ResponseDTO.builder()
                .status("error")
                .statusCode(status.value())
                .message(message)
                .build();
    }
}
