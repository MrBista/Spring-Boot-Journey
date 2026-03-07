package com.bisma.foundation.fundamental_spring_boot.controller;

import com.bisma.foundation.fundamental_spring_boot.Enum.ErrorCode;
import com.bisma.foundation.fundamental_spring_boot.dto.ApiResponse;
import com.bisma.foundation.fundamental_spring_boot.dto.req.RegisterReqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping
    public ResponseEntity<ApiResponse<?>> testGetSuccess() {
        return ResponseEntity.ok(ApiResponse.success("Success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> testResponseGagal(@PathVariable Long id) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.BAD_REQUEST));
    }

}
