package com.practice.demopractice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private String status;
    private Integer statusCode;
    private Object data;
    private String message;
    private Object pageData;
}
