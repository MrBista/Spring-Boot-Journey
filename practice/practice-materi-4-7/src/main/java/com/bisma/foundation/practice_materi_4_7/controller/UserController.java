package com.bisma.foundation.practice_materi_4_7.controller;

import com.bisma.foundation.practice_materi_4_7.dto.ApiResponse;
import com.bisma.foundation.practice_materi_4_7.dto.UserReqDTO;
import com.bisma.foundation.practice_materi_4_7.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success(userService.findAllUsers())
        );
    }



    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUser(@Valid  @RequestBody UserReqDTO userReqDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(userService.saveUser(userReqDTO)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(@Valid  @RequestBody UserReqDTO userReqDTO, @PathVariable Long id) {

        userService.updateUser(userReqDTO, id);

        return ResponseEntity
                .ok(ApiResponse.success(true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {

        userService.deleteUserById(id);

        return ResponseEntity.ok(ApiResponse.success(true));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable Long id) {
        return ResponseEntity
                .ok(
                        ApiResponse.success(userService.findUserById(id))
                );
    }



}
