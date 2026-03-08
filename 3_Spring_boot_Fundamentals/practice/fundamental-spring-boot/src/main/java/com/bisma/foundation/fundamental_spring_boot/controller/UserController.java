package com.bisma.foundation.fundamental_spring_boot.controller;

import com.bisma.foundation.fundamental_spring_boot.dto.ApiResponse;
import com.bisma.foundation.fundamental_spring_boot.dto.UserResponseDto;
import com.bisma.foundation.fundamental_spring_boot.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.findUserById(2);
        return ResponseEntity.ok(ApiResponse.success(userResponseDto));
    }
}
