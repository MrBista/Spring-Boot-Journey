package com.bisma.foundation.practice_materi_4_7.services;

import com.bisma.foundation.practice_materi_4_7.dto.UserResponseDTO;
import com.bisma.foundation.practice_materi_4_7.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> findAllUsers();
    UserResponseDTO findUserById(Long id);
    UserResponseDTO saveUser(User user);
    void updateUser(User user);
    void deleteUserById(Long id);
}
